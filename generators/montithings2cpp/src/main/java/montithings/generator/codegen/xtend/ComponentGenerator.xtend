// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import montiarc._symboltable.ComponentSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Subcomponents
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.Setup
import montithings.generator.codegen.xtend.util.Init
import montithings.generator.codegen.xtend.behavior.AutomatonGenerator
import java.util.List
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import de.monticore.symboltable.types.JFieldSymbol
import montiarc._symboltable.ComponentSymbolReference
import de.monticore.mcexpressions._ast.ASTExpression
import de.monticore.prettyprint.IndentPrinter
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import montithings.generator.visitor.CDAttributeGetterTransformationVisitor

class ComponentGenerator {
	
	def static generateHeader(ComponentSymbol comp, String compname, HashMap<String, String> interfaceToImplementation) {
		var ComponentHelper helper = new ComponentHelper(comp)
    var HashSet<String> compIncludes = new HashSet<String>()
    for (subcomponent : comp.subComponents) {
      compIncludes.add('''#include "«ComponentHelper.getPackagePath(comp, subcomponent)»«ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, interfaceToImplementation, false)».h"''')
		}
		return '''
		#pragma once
		#include "IComponent.h"
		#include "Port.h"
		#include <string>
		#include <map>
		#include <vector>
		#include <list>
		#include <set>
		#include "IncomingIPCPort.h"
		#include "OutgoingIPCPort.h"
		#include "IncomingWSPort.h"
		#include "OutgoingWSPort.h"
		#include<thread>
		#include <boost/uuid/uuid.hpp>
		#include <boost/uuid/uuid_generators.hpp>
		«Utils.printCPPImports(comp)»
		
		
		«IF comp.isDecomposed»
		«FOR include : compIncludes»
		«include»
		«ENDFOR»
		«ELSE»
		#include "«compname»Impl.h"
		«ENDIF»

		«Utils.printTemplateArguments(comp)»
		class «compname» : IComponent «IF comp.superComponent.present» , «Utils.printSuperClassFQ(comp)»
		            «IF comp.superComponent.get.hasFormalTypeParameters»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
		              «scTypeParams»«ENDFOR»>
		            «ENDIF»«ENDIF»
		{
		private:
			«Ports.printVars(comp.ports)»
			«Ports.printResourcePortVars(ComponentHelper.getResourcePortsInComponent(comp))»
			«Utils.printVariables(comp)»
			«Utils.printConfigParameters(comp)»
			std::vector< std::thread > threads;
			«IF comp.isDecomposed»
			«IF comp.getStereotype().containsKey("timesync") && !comp.getStereotype().containsKey("deploy")»
			void run();
			«ENDIF»
			«Subcomponents.printVars(comp, interfaceToImplementation)»
			«ELSE»

			«compname»Impl«Utils.printFormalTypeParameters(comp)» «Identifier.behaviorImplName»;
			void initialize();
			void setResult(«compname»Result«Utils.printFormalTypeParameters(comp)» result);
			void run();
			«ENDIF»
			
			
		public:
			«Ports.printMethodHeaders(comp.ports)»
			«Ports.printResourcePortMethodHeaders(ComponentHelper.getResourcePortsInComponent(comp))»
			«IF comp.isDecomposed»	
			«ENDIF»
			
			«compname»(«Utils.printConfigurationParametersAsList(comp)»);
			
			void setUp() override;
			void init() override;
			void compute() override;
			void start() override;
			
		};            
		            
		«IF Utils.hasTypeParameters(comp)»
      «generateBody(comp, compname)»
    «ENDIF»
		
		'''
	}

	def static generateImplementationFile(ComponentSymbol comp, String compname) {
	  return '''
  	#include "«compname».h"
  	«IF !Utils.hasTypeParameters(comp)»
    «generateBody(comp, compname)»
    «ENDIF»
    '''
	}
	
	def static generateBody(ComponentSymbol comp, String compname) {
		return '''
		«Ports.printMethodBodies(comp.ports, comp, compname)»
		«Ports.printResourcePortMethodBodies(ComponentHelper.getResourcePortsInComponent(comp),comp, compname)»
				
		«IF comp.isDecomposed»
		«IF comp.getStereotype().containsKey("timesync") && !comp.getStereotype().containsKey("deploy")»
		«printRun(comp, compname)»
		«ENDIF»
		«printComputeDecomposed(comp, compname)»
		«printStartDecomposed(comp, compname)»
		«ELSE»
		«printComputeAtomic(comp, compname)»
		«printStartAtomic(comp, compname)»
		«printRun(comp, compname)»

		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::initialize(){
			«compname»Result«Utils.printFormalTypeParameters(comp)» result = «Identifier.behaviorImplName».getInitialValues();
			setResult(result);
		}

		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::setResult(«compname»Result«Utils.printFormalTypeParameters(comp)» result){
			«FOR portOut : comp.outgoingPorts»
			this->getPort«portOut.name.toFirstUpper»()->setNextValue(result.get«portOut.name.toFirstUpper»());
            «ENDFOR»
		}
		«ENDIF»

		«Setup.print(comp, compname)»

		«Init.print(comp, compname)»
		
		«printConstructor(comp, compname)»
		'''
	}
	

	def static printConstructor(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		«compname»«Utils.printFormalTypeParameters(comp)»::«compname»(«Utils.printConfigurationParametersAsList(comp)»)
		«IF !comp.configParameters.isEmpty || !comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty].isEmpty»
    :
    «ENDIF»
		«Subcomponents.printInitializerList(comp)»
		«IF !comp.configParameters.isEmpty && !comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty].isEmpty»,«ENDIF»
		«FOR param : comp.configParameters SEPARATOR ','»
      «param.name» («param.name»)
    «ENDFOR»
		{
			«IF comp.superComponent.present»
			super(«FOR inhParam : getInheritedParams(comp, compname) SEPARATOR ','» «inhParam» «ENDFOR»);
			«ENDIF»
			«IF comp.isAtomic»
			«compname»Impl«Utils.printFormalTypeParameters(comp, false)» behav«IF comp.hasConfigParameters»(
			            «FOR param : comp.configParameters SEPARATOR ','»
			              «param.name»
			            «ENDFOR»
					    )«ENDIF»;
  «Identifier.behaviorImplName» = behav;
			        «ENDIF»
		}
		'''
	}
	
	def static printComputeAtomic(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::compute(){
			«IF comp.allIncomingPorts.length > 0 && !ComponentHelper.hasSyncGroups(comp)»
			if («FOR inPort : comp.allIncomingPorts SEPARATOR ' || '»getPort«inPort.name.toFirstUpper»()->hasValue(portUuid«inPort.name.toFirstUpper»)«ENDFOR»)
			«ENDIF»
			«IF ComponentHelper.hasSyncGroups(comp)»
			if ( 
				«FOR syncGroup : ComponentHelper.getSyncGroups(comp)  SEPARATOR ' || '»
				(«FOR port : syncGroup SEPARATOR ' && '» getPort«port.toFirstUpper»()->hasValue(portUuid«port.toFirstUpper») «ENDFOR»)
				«ENDFOR»
				«IF ComponentHelper.getPortsNotInSyncGroup(comp).length() > 0»
				|| «FOR port : ComponentHelper.getPortsNotInSyncGroup(comp) SEPARATOR ' || '» getPort«port.name.toFirstUpper»()->hasValue(portUuid«port.name.toFirstUpper»)«ENDFOR»
				<«ENDIF»
			)
			«ENDIF»
			{
				«IF !ComponentHelper.usesBatchMode(comp)»
				«compname»Input«Utils.printFormalTypeParameters(comp)» input«IF !comp.allIncomingPorts.empty»(«FOR inPort : comp.allIncomingPorts SEPARATOR ','»getPort«inPort.name.toFirstUpper»()->getCurrentValue(portUuid«inPort.name.toFirstUpper»)«ENDFOR»)«ENDIF»;
				«ELSE»
				«compname»Input«Utils.printFormalTypeParameters(comp)» input;
				«FOR inPort : ComponentHelper.getPortsInBatchStatement(comp)»
				while(getPort«inPort.name.toFirstUpper»()->hasValue(portUuid«inPort.name.toFirstUpper»)){
					input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(portUuid«inPort.name.toFirstUpper»));
				}
				«ENDFOR»
				«FOR inPort : ComponentHelper.getPortsNotInBatchStatements(comp)»
				input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(portUuid«inPort.name.toFirstUpper»));
				«ENDFOR»
				«ENDIF»
				«compname»Result«Utils.printFormalTypeParameters(comp)» result;
				«IF !ComponentHelper.hasExecutionStatement(comp)»
				result = «Identifier.behaviorImplName».compute(input);
				«ELSE»
				
				«FOR statement : ComponentHelper.getExecutionStatements(comp) SEPARATOR " else "»
				if (
					«FOR port : ComponentHelper.getPortsInGuardExpression(statement) SEPARATOR ' && '»
					«IF !ComponentHelper.isBatchPort(port, comp)»
					input.get«port.name.toFirstUpper»()
					«ENDIF»
					«ENDFOR»
					«IF ComponentHelper.getPortsInGuardExpression(statement).length() > 0»&&«ENDIF» «printExpression(statement.guard)»
					)
				{
					result = «Identifier.behaviorImplName».«statement.method»(input);	
				}
				«ENDFOR»
				«IF ComponentHelper.getElseStatement(comp) !== null»
				else {
					result = «Identifier.behaviorImplName».«ComponentHelper.getElseStatement(comp).method»(input);
					}

				«ENDIF»
				«ENDIF»
				setResult(result);				
			}
		}
		'''
	}
		
	
	def static printComputeDecomposed(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::compute(){
			«FOR subcomponent : comp.subComponents»
			this->«subcomponent.name».compute();
	        «ENDFOR»
					
		}
		'''
	}
	
	def static printStartDecomposed(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::start(){
			«IF comp.getStereotype().containsKey("timesync") && !comp.getStereotype().containsKey("deploy")»
			threads.push_back(std::thread{&«compname»«Utils.printFormalTypeParameters(comp)»::run, this});
			«ELSE»
			«FOR subcomponent : comp.subComponents»
			this->«subcomponent.name».start();
	        «ENDFOR»
			«ENDIF»		
		}
		'''
	}
	
	def static printStartAtomic(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::start(){
			threads.push_back(std::thread{&«compname»«Utils.printFormalTypeParameters(comp)»::run, this});
		}
		'''
	}
	
	def static printRun(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::run(){
			cout << "Thread for «compname» started\n";
			
			while (true)
				{
					auto end = std::chrono::high_resolution_clock::now() 
						+ «ComponentHelper.getExecutionIntervalMethod(comp)»;
					this->compute();
					
					do {
					  std::this_thread::yield();
					  std::this_thread::sleep_for(std::chrono::milliseconds(1));
					} while (std::chrono::high_resolution_clock::now()  < end);
				}
		}

		'''
	}
	
	def private static List<String> getInheritedParams(ComponentSymbol component, String compname) {
    var List<String> result = new ArrayList;
    var List<JFieldSymbol> configParameters = component.getConfigParameters();
    if (component.getSuperComponent().isPresent()) {
      var ComponentSymbolReference superCompReference = component.getSuperComponent().get();
      var List<JFieldSymbol> superConfigParams = superCompReference.getReferencedSymbol().getConfigParameters();
      if (!configParameters.isEmpty()) {
        for (var i = 0; i < superConfigParams.size(); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }
  
  def private static String printExpression(ASTExpression expr, boolean isAssignment) {
    var IndentPrinter printer = new IndentPrinter();
    var JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    if (isAssignment) {
      prettyPrinter = new CDAttributeGetterTransformationVisitor(printer);
    }
    expr.accept(prettyPrinter);
    return printer.getContent();
  }

  def private static String printExpression(ASTExpression expr) {
    return printExpression(expr, true);
  }
	
}