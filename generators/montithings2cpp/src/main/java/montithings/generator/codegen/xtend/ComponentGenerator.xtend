// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import montithings._symboltable.ComponentSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Subcomponents
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.Setup
import montithings.generator.codegen.xtend.util.Init
import java.util.List
import java.util.ArrayList
import java.util.HashMap
import de.monticore.symboltable.types.JFieldSymbol
import montiarc._symboltable.ComponentSymbolReference
import de.monticore.mcexpressions._ast.ASTExpression
import de.monticore.prettyprint.IndentPrinter
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import montithings.generator.visitor.CDAttributeGetterTransformationVisitor

class ComponentGenerator {
	
	def static generateHeader(ComponentSymbol comp, String compname, HashMap<String, String> interfaceToImplementation) {
		var ComponentHelper helper = new ComponentHelper(comp)
	    
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
		#include <thread>
		#include "sole/sole.hpp"
		#include <iostream>
		«Utils.printCPPImports(comp)»
		«Ports.printIncludes(comp)»
		
		«IF comp.isDecomposed»
		«Subcomponents.printIncludes(comp, compname, interfaceToImplementation)»
		«ELSE»
		#include "«compname»Impl.h"
		«ENDIF»
		
		«Utils.printNamespaceStart(comp)»

		«Utils.printTemplateArguments(comp)»
		class «compname» : IComponent «IF comp.superComponent.present» , «Utils.printSuperClassFQ(comp)»
		            «IF comp.superComponent.get.hasFormalTypeParameters»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
		              «scTypeParams»«ENDFOR»>
		            «ENDIF»«ENDIF»
		{
		private:
			«Ports.printVars(comp, comp.ports)»
			«Ports.printResourcePortVars(ComponentHelper.getResourcePortsInComponent(comp))»
			«Utils.printVariables(comp)»
			«Utils.printConfigParameters(comp)»
			std::vector< std::thread > threads;
			TimeMode timeMode = «IF comp.getStereotype().containsKey("timesync")»TIMESYNC«ELSE»EVENTBASED«ENDIF»;
			«IF comp.isDecomposed»
			«IF comp.getStereotype().containsKey("timesync") && !comp.isApplication»
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
			«compname»(«Utils.printConfigurationParametersAsList(comp)»);
			
			void setUp(TimeMode enclosingComponentTiming) override;
			void init() override;
			void compute() override;
			bool shouldCompute();
			void start() override;
		};
		            
		«IF Utils.hasTypeParameters(comp)»
	      «generateBody(comp, compname)»
	    «ENDIF»

	    «Utils.printNamespaceEnd(comp)»
		'''
	}

	def static generateImplementationFile(ComponentSymbol comp, String compname) {
	  return '''
  	#include "«compname».h"
  	#include <regex>
  	«Utils.printNamespaceStart(comp)»
  	«IF !Utils.hasTypeParameters(comp)»
    «generateBody(comp, compname)»
    «ENDIF»
    «Utils.printNamespaceEnd(comp)»
    '''
	}
	
	def static generateBody(ComponentSymbol comp, String compname) {
		return '''
		«Ports.printMethodBodies(comp.ports, comp, compname)»
		«Ports.printResourcePortMethodBodies(ComponentHelper.getResourcePortsInComponent(comp),comp, compname)»
				
		«IF comp.isDecomposed»
		«IF comp.getStereotype().containsKey("timesync") && !comp.isApplication»
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
		
		«printShouldComputeCheck(comp, compname)»

		«Setup.print(comp, compname)»

		«Init.print(comp, compname)»
		
		«printConstructor(comp, compname)»
		'''
	}
	

	def static printConstructor(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		«compname»«Utils.printFormalTypeParameters(comp)»::«compname»(«Utils.printConfigurationParametersAsList(comp)»)
		«IF comp.isAtomic || !comp.configParameters.isEmpty || !comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty].isEmpty»
		:
    	«ENDIF»
    	«IF comp.isAtomic»
			«printBehaviorInitializerListEntry(comp, compname)»
        «ENDIF»
    	«IF !comp.configParameters.isEmpty»,«ENDIF»
		«Subcomponents.printInitializerList(comp)»
		«IF !comp.configParameters.isEmpty && !comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty].isEmpty»,«ENDIF»
		«FOR param : comp.configParameters SEPARATOR ','»
      	«param.name» («param.name»)
    	«ENDFOR»
		{
			«IF comp.superComponent.present»
			super(«FOR inhParam : getInheritedParams(comp, compname) SEPARATOR ','» «inhParam» «ENDFOR»);
			«ENDIF»
		}
		'''
	}
	
	def static printBehaviorInitializerListEntry(ComponentSymbol comp, String compname) {
		return '''
		«Identifier.behaviorImplName»(«compname»Impl«Utils.printFormalTypeParameters(comp, false)»(
		«IF comp.hasConfigParameters»
	        «FOR param : comp.configParameters SEPARATOR ','»
	          «param.name»
	        «ENDFOR»
    «ENDIF»
	))'''.toString().replace("\n", "")
	}
	
	def static printComputeAtomic(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::compute() {
			if (shouldCompute())
			{
				«printComputeInputs(comp, compname)»
				«compname»Result«Utils.printFormalTypeParameters(comp)» result;
				«FOR port: comp.incomingPorts»
        «ComponentHelper.printPortValuecheck(comp, port)»
        «ENDFOR»
				«printAssumptionsCheck(comp, compname)»
				«IF !ComponentHelper.hasExecutionStatement(comp)»
				result = «Identifier.behaviorImplName».compute(input);
				«ELSE»
				«printIfThenElseExecution(comp, compname)»
				«ENDIF»
				«FOR port: comp.outgoingPorts»
          «ComponentHelper.printPortValuecheck(comp, port)»
        «ENDFOR»
				«printGuaranteesCheck(comp, compname)»
				setResult(result);				
			}
		}
		'''
	}
	
	def static printComputeInputs(ComponentSymbol comp, String compname) {
		return printComputeInputs(comp, compname, false);
	}
	
	def static printComputeInputs(ComponentSymbol comp, String compname, boolean isMonitor) {
		return '''
		«IF !ComponentHelper.usesBatchMode(comp)»
		«compname»Input«Utils.printFormalTypeParameters(comp)» input«IF !comp.allIncomingPorts.empty»(«FOR inPort : comp.allIncomingPorts SEPARATOR ','»getPort«inPort.name.toFirstUpper»()->getCurrentValue(port«IF isMonitor»Monitor«ENDIF»Uuid«inPort.name.toFirstUpper»)«ENDFOR»)«ENDIF»;
		«ELSE»
		«compname»Input«Utils.printFormalTypeParameters(comp)» input;
		«FOR inPort : ComponentHelper.getPortsInBatchStatement(comp)»
		while(getPort«inPort.name.toFirstUpper»()->hasValue(portUuid«inPort.name.toFirstUpper»)){
			input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(port«IF isMonitor»Monitor«ENDIF»Uuid«inPort.name.toFirstUpper»));
		}
		«ENDFOR»
		«FOR inPort : ComponentHelper.getPortsNotInBatchStatements(comp)»
		input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(port«IF isMonitor»Monitor«ENDIF»Uuid«inPort.name.toFirstUpper»));
		«ENDFOR»
		«ENDIF»
		'''
	}
	
	def static printShouldComputeCheck(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		bool «compname»«Utils.printFormalTypeParameters(comp)»::shouldCompute() {
			«IF comp.allIncomingPorts.length > 0 && !ComponentHelper.hasSyncGroups(comp)»
			if (timeMode == TIMESYNC || «FOR inPort : comp.allIncomingPorts SEPARATOR ' || '»getPort«inPort.name.toFirstUpper»()->hasValue(portUuid«inPort.name.toFirstUpper»)«ENDFOR»)
			{ return true; }
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
			{ return true; }
			«ENDIF»
			«IF comp.allIncomingPorts.length == 0»
			return true;
			«ELSE»
			return false;
			«ENDIF»
		}
		'''
	}
	
	def static printAssumptionsCheck(ComponentSymbol comp, String compname) {
		var assumptions = ComponentHelper.getAssumptions(comp);
		return '''
		«FOR statement : assumptions»
		if (
		«FOR port : statement.portsInGuardExpression SEPARATOR ' && '»
			«IF !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.name)»
				input.get«port.name.toFirstUpper»()
			«ELSE»
				true // presence of value on port «port.name» not checked as it is compared to NoData
			«ENDIF»
		«ENDFOR» && 
		!(
			«printExpression(statement.guard)»
		)) {
			std::stringstream error;
			error << "Violated assumption «printExpression(statement.guard, false)» on component «comp.packageName».«compname»" << std::endl;
			error << "Input port values: " << std::endl;
			«FOR inPort : ComponentHelper.getPortsNotInBatchStatements(comp)»
			if (input.get«inPort.name.toFirstUpper» ().has_value()) {
				error << "Port \"«inPort.name»\": " << input.get«inPort.name.toFirstUpper» ().value() << std::endl; 
			} else {
				error << "Port \"«inPort.name»\": No data." << std::endl;
			}
			«ENDFOR»
			«FOR inPort : ComponentHelper.getPortsInBatchStatement(comp)»
			if (input.get«inPort.name.toFirstUpper» ().has_value()) {
				error << "Port \"«inPort.name»\": " << input.get«inPort.name.toFirstUpper» () << std::endl; 
			} else {
				error << "Port \"«inPort.name»\": No data." << std::endl;
			}
			«ENDFOR»
			throw std::runtime_error(error.str ());
		}
		«ENDFOR»
		'''
	}
	
	def static printGuaranteesCheck(ComponentSymbol comp, String compname) {
		var guarantees = ComponentHelper.getGuarantees(comp);
		return '''
		«FOR statement : guarantees»
		if (
		«FOR port : statement.portsInGuardExpression SEPARATOR ' && '»
			«IF !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.name)»
				«IF port.isIncoming»
				input.get«port.name.toFirstUpper»()
				«ELSE»
				result.get«port.name.toFirstUpper»()
				«ENDIF»
			«ELSE»
				true // presence of value on port «port.name» not checked as it is compared to NoData
			«ENDIF»
		«ENDFOR» && 
		!(
			«printExpression(statement.guard)»
		)) {
			std::stringstream error;
			error << "Violated guarantee «printExpression(statement.guard, false)» on component «comp.packageName».«compname»" << std::endl;
			error << "Port values: " << std::endl;
			«FOR inPort : ComponentHelper.getPortsNotInBatchStatements(comp)»
			if (input.get«inPort.name.toFirstUpper» ().has_value()) {
				error << "In port \"«inPort.name»\": " << input.get«inPort.name.toFirstUpper» ().value() << std::endl; 
			} else {
				error << "In port \"«inPort.name»\": No data." << std::endl;
			}
			«ENDFOR»
			«FOR inPort : ComponentHelper.getPortsInBatchStatement(comp)»
			if (input.get«inPort.name.toFirstUpper» ().has_value()) {
				error << "In port \"«inPort.name»\": " << input.get«inPort.name.toFirstUpper» () << std::endl; 
			} else {
				error << "In port \"«inPort.name»\": No data." << std::endl;
			}
			«ENDFOR»
			«FOR outPort : comp.allOutgoingPorts»
			if (result.get«outPort.name.toFirstUpper» ().has_value()) {
				error << "Out port \"«outPort.name»\": " << result.get«outPort.name.toFirstUpper» ().value() << std::endl; 
			} else {
				error << "Out port \"«outPort.name»\": No data." << std::endl;
			}
			«ENDFOR»
			throw std::runtime_error(error.str ());
		}
		«ENDFOR»
		'''
	}
	
	def static printIfThenElseExecution(ComponentSymbol comp, String compname) {
		return '''
		«FOR statement : ComponentHelper.getExecutionStatements(comp) SEPARATOR " else "»
		if (
			«FOR port : statement.portsInGuardExpression SEPARATOR ' && '»
			«IF !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard.expression, port.name)»
			input.get«port.name.toFirstUpper»()
			«ELSE»
			true // presence of value on port «port.name» not checked as it is compared to NoData
			«ENDIF»
			«ENDFOR»
			«IF statement.portsInGuardExpression.length() > 0»&&«ENDIF» «printExpression(statement.guard.expression)»
			)
		{
			result = «Identifier.behaviorImplName».«statement.method»(input);	
		}
		«ENDFOR»
		«IF ComponentHelper.getElseStatement(comp) !== null»
		else {
			result = «Identifier.behaviorImplName».«ComponentHelper.getElseStatement(comp).get.method»(input);
			}
		«ENDIF»
		'''
	}
		
	
	def static printComputeDecomposed(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::compute(){
			if (shouldCompute()) {
			
			«printComputeInputs(comp, compname)»
			«FOR port: comp.incomingPorts»
			«ComponentHelper.printPortValuecheck(comp, port)»
			«ENDFOR»
			«printAssumptionsCheck(comp, compname)»
			
			«FOR subcomponent : comp.subComponents»
				this->«subcomponent.name».compute();
      «ENDFOR»

      «printComputeResults(comp, compname, true)»
      «FOR port: comp.outgoingPorts»
        «ComponentHelper.printPortValuecheck(comp, port)»
      «ENDFOR»
      «printGuaranteesCheck(comp, compname)»
			}
		}
		'''
	}
	
	def static printComputeResults(ComponentSymbol comp, String compname, boolean isMonitor) {
		return '''
		«compname»Result«Utils.printFormalTypeParameters(comp)» result;
		«FOR outPort : comp.allOutgoingPorts»
		if (getPort«outPort.name.toFirstUpper»()->hasValue(port«IF isMonitor»Monitor«ENDIF»Uuid«outPort.name.toFirstUpper»)) {
			result.set«outPort.name.toFirstUpper»(getPort«outPort.name.toFirstUpper»()->getCurrentValue(port«IF isMonitor»Monitor«ENDIF»Uuid«outPort.name.toFirstUpper»).value());
		}
		«ENDFOR»
		'''
	}
	
	def static printStartDecomposed(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::start(){
			«IF comp.getStereotype().containsKey("timesync") && !comp.isApplication»
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
			std::cout << "Thread for «compname» started\n";
			
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