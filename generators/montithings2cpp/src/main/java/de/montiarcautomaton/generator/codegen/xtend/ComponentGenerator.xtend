package de.montiarcautomaton.generator.codegen.xtend

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Ports
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.codegen.xtend.util.Subcomponents
import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.montiarcautomaton.generator.codegen.xtend.util.Setup
import de.montiarcautomaton.generator.codegen.xtend.util.Init
import java.util.List
import java.util.ArrayList
import de.monticore.symboltable.types.JFieldSymbol
import montiarc._symboltable.ComponentSymbolReference
import de.monticore.mcexpressions._ast.ASTExpression
import de.monticore.prettyprint.IndentPrinter
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import de.montiarcautomaton.generator.visitor.CDAttributeGetterTransformationVisitor

class ComponentGenerator {
	
	def static generateHeader(ComponentSymbol comp) {
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
		#include<thread>
		#include <boost/uuid/uuid.hpp>
		#include <boost/uuid/uuid_generators.hpp>
		«Utils.printCPPImports(comp)»
		
		
		«IF comp.isDecomposed»
		«FOR subcomponent : comp.subComponents»
		#include "«ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent)».h"
		«ENDFOR»
		«ELSE»
		#include "«comp.name»Impl.h"
		«ENDIF»
		
		
		class «comp.name»«Utils.printFormalTypeParameters(comp)» : IComponent «IF comp.superComponent.present» , «Utils.printSuperClassFQ(comp)» 
		            «IF comp.superComponent.get.hasFormalTypeParameters»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
		              «scTypeParams»«ENDFOR»>
		            «ENDIF»«ENDIF»
		{
		private:
			boost::uuids::uuid uuid = boost::uuids::random_generator()();
			«Ports.printVars(comp.ports)»
			«Ports.printResourcePortVars(ComponentHelper.getResourcePortsInComponent(comp))»
			«Utils.printVariables(comp)»
			«Utils.printConfigParameters(comp)»
			std::vector< std::thread > threads;
			«IF comp.isDecomposed»
			«IF comp.getStereotype().containsKey("timesync") && !comp.getStereotype().containsKey("deploy")»
			void run();
			«ENDIF»
			«Subcomponents.printVars(comp)»
			«ELSE»

			«comp.name»Impl «Identifier.behaviorImplName»;
			void initialize();
			void setResult(«comp.name»Result result);
			void run();
			«ENDIF»
			
			
		public:
			«Ports.printMethodHeaders(comp.ports)»
			«Ports.printResourcePortMethodHeaders(ComponentHelper.getResourcePortsInComponent(comp))»
			«IF comp.isDecomposed»	
			«ENDIF»
			
			«comp.name»(«Utils.printConfiurationParametersAsList(comp)»);
			
			void setUp() override;
			void init() override;
			void compute() override;
			void start() override;
			
		};            
		            
		            
		
		'''
	}
	
	def static generateBody(ComponentSymbol comp) {
		return '''
		#include "«comp.name».h"
		
		«Ports.printMethodBodies(comp.ports, comp)»
		«Ports.printResourcePortMethodBodies(ComponentHelper.getResourcePortsInComponent(comp),comp)»
				
		«IF comp.isDecomposed»
		«IF comp.getStereotype().containsKey("timesync") && !comp.getStereotype().containsKey("deploy")»
		«printRun(comp)»
		«ENDIF»
		«printComputeDecomposed(comp)»
		«printStartDecomposed(comp)»
		«ELSE»
		«printComputeAtomic(comp)»
		«printStartAtomic(comp)»
		«printRun(comp)»
		
		void «comp.name»::initialize(){
			«comp.name»Result result = «Identifier.behaviorImplName».getInitialValues();
			setResult(result);
		}
		
		void «comp.name»::setResult(«comp.name»Result result){
			«FOR portOut : comp.outgoingPorts»
			this->getPort«portOut.name.toFirstUpper»()->setNextValue(result.get«portOut.name.toFirstUpper»());
            «ENDFOR»
			
		}
		
		
		«ENDIF»
		«Setup.print(comp)»
		
		«Init.print(comp)»
		
		«printConstructor(comp)»
		'''
	}
	

	def static printConstructor(ComponentSymbol comp) {
		return '''
		«comp.name»::«comp.name»(«Utils.printConfiurationParametersAsList(comp)»){
			«IF comp.superComponent.present»
			super(«FOR inhParam : getInheritedParams(comp) SEPARATOR ','» «inhParam» «ENDFOR»);
			«ENDIF»
			«IF comp.isAtomic»
			«comp.name»Impl«Utils.printFormalTypeParameters(comp)» behav«IF comp.hasConfigParameters»(
			            «FOR param : comp.configParameters SEPARATOR ','»
			              «param.name»
						            «ENDFOR»
					    )«ENDIF»;
          	«Identifier.behaviorImplName» = behav;
			        «ENDIF»
			«FOR param : comp.configParameters»
			this.«param.name» = «param.name»;
			«ENDFOR»
			
			
		}
		'''
	}
	
	def static printComputeAtomic(ComponentSymbol comp) {
		return '''
		void «comp.name»::compute(){
			«IF comp.allIncomingPorts.length > 0 && !ComponentHelper.hasSyncGroups(comp)»
			if («FOR inPort : comp.allIncomingPorts SEPARATOR ' || '»getPort«inPort.name.toFirstUpper»()->hasValue(uuid)«ENDFOR»)
			«ENDIF»
			«IF ComponentHelper.hasSyncGroups(comp)»
			if ( 
			«FOR syncGroup : ComponentHelper.getSyncGroups(comp)  SEPARATOR ' || '»
			(«FOR port : syncGroup SEPARATOR ' && '» getPort«port.toFirstUpper»()->hasValue(uuid) «ENDFOR»)
			«ENDFOR»
			«IF ComponentHelper.getPortsNotInSyncGroup(comp).length() > 0»
			|| «FOR port : ComponentHelper.getPortsNotInSyncGroup(comp) SEPARATOR ' || '» getPort«port.name.toFirstUpper»()->hasValue(uuid)«ENDFOR»
			«ENDIF»
			)
			«ENDIF»
			{
				«IF !ComponentHelper.usesBatchMode(comp)»
				«comp.name»Input input«IF !comp.allIncomingPorts.empty»(«FOR inPort : comp.allIncomingPorts SEPARATOR ','»getPort«inPort.name.toFirstUpper»()->getCurrentValue(uuid)«ENDFOR»)«ENDIF»;
				«ELSE»
				«comp.name»Input input;
				«FOR inPort : ComponentHelper.getPortsInBatchStatement(comp)»
				while(getPort«inPort.name.toFirstUpper»()->hasValue(uuid)){
					input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(uuid));
				}
				«ENDFOR»
				«FOR inPort : ComponentHelper.getPortsNotInBatchStatements(comp)»
				input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(uuid));
				«ENDFOR»
				«ENDIF»
				«comp.name»Result result;
				«IF !ComponentHelper.hasExecutionStatement(comp)»
				result = «Identifier.behaviorImplName».compute(input);
				«ELSE»
				
				«FOR statement : ComponentHelper.getExecutionStatements(comp)»
				if («FOR port : ComponentHelper.getPortsInGuardExpression(statement) SEPARATOR ' && '»
					«IF !ComponentHelper.isBatchPort(port, comp)»
					input.get«port.name.toFirstUpper»()
					«ENDIF»
					«ENDFOR»
					«IF ComponentHelper.getPortsInGuardExpression(statement).length() > 0»&&«ENDIF» «printExpression(statement.guard)»){
					result = «Identifier.behaviorImplName».«statement.method»(input);	
				}
				«ENDFOR»
				«ENDIF»
				setResult(result);				
			}
		}

		'''
	}
		
	
	def static printComputeDecomposed(ComponentSymbol comp) {
		return '''
		void «comp.name»::compute(){
			«FOR subcomponent : comp.subComponents»
			this->«subcomponent.name».compute();
	        «ENDFOR»
					
		}

		'''
	}
	
	def static printStartDecomposed(ComponentSymbol comp) {
		return '''
		void «comp.name»::start(){
			«IF comp.getStereotype().containsKey("timesync") && !comp.getStereotype().containsKey("deploy")»
			threads.push_back(std::thread{&«comp.name»::run, this});
			«ELSE»
			«FOR subcomponent : comp.subComponents»
			this->«subcomponent.name».start();
	        «ENDFOR»
			«ENDIF»		
		}

		'''
	}
	
	def static printStartAtomic(ComponentSymbol comp) {
		return '''
		void «comp.name»::start(){
			threads.push_back(std::thread{&«comp.name»::run, this});
					
		}

		'''
	}
	
	def static printRun(ComponentSymbol comp) {
		return '''
		void «comp.name»::run(){
			cout << "Thread for «comp.name» started\n";
			
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
	
	def private static List<String> getInheritedParams(ComponentSymbol component) {
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