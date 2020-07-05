// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils
//import montithings.generator.codegen.xtend.util.ValueCheck
import montithings.generator.codegen.xtend.util.Subcomponents
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.Setup
import montithings.generator.codegen.xtend.util.Init
import montithings.generator.codegen.TargetPlatform
import java.util.List
import java.util.ArrayList
import java.util.HashMap
import arcbasis._symboltable.ComponentTypeSymbolLoader
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

class ComponentGenerator {
	
	def static generateHeader(ComponentTypeSymbol comp, String compname, HashMap<String, String> interfaceToImplementation, TargetPlatform platform) {
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
		«IF platform != TargetPlatform.ARDUINO»
		#include "IPCPort.h"
		#include "WSPort.h"
		«ENDIF»
		#include <thread>
		#include "sole/sole.hpp"
		#include <iostream>
		«Ports.printIncludes(comp)»
		
		«IF comp.isDecomposed»
		«Subcomponents.printIncludes(comp, compname, interfaceToImplementation)»
		«ELSE»
		#include "«compname»Impl.h"
		«ENDIF»
		
		«Utils.printNamespaceStart(comp)»

		«Utils.printTemplateArguments(comp)»
		class «compname» : IComponent «IF comp.presentParentComponent» , «Utils.printSuperClassFQ(comp)»
		            «IF comp.parent.loadedSymbol.hasTypeParameter»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
		              «scTypeParams»«ENDFOR»>
		            «ENDIF»«ENDIF»
		{
		private:
			«Ports.printVars(comp, comp.ports)»
			«Utils.printVariables(comp)»
			«Utils.printConfigParameters(comp)»
			std::vector< std::thread > threads;
			TimeMode timeMode = «IF ComponentHelper.isTimesync(comp)»TIMESYNC«ELSE»EVENTBASED«ENDIF»;
			«IF comp.isDecomposed»
			«IF ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)»
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
			«compname»(«Utils.printConfigurationParametersAsList(comp)»);
			
			void setUp(TimeMode enclosingComponentTiming) override;
			void init() override;
			void compute() override;
			bool shouldCompute();
			void start() override;
		};
		            
		«IF comp.hasTypeParameter()»
	      «generateBody(comp, compname)»
	    «ENDIF»

	    «Utils.printNamespaceEnd(comp)»
		'''
	}

	def static generateImplementationFile(ComponentTypeSymbol comp, String compname) {
	  return '''
  	#include "«compname».h"
  	#include <regex>
  	«Utils.printNamespaceStart(comp)»
  	«IF !comp.hasTypeParameter()»
    «generateBody(comp, compname)»
    «ENDIF»
    «Utils.printNamespaceEnd(comp)»
    '''
	}
	
	def static generateBody(ComponentTypeSymbol comp, String compname) {
		return '''
		«Ports.printMethodBodies(comp.ports, comp, compname)»
				
		«IF comp.isDecomposed»
		«IF ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)»
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
			«FOR port : comp.incomingPorts»
			getPort«port.name.toFirstUpper» ()->registerListeningPort (this->getUuid ());
			«ENDFOR»
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
	

	def static printConstructor(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		«compname»«Utils.printFormalTypeParameters(comp)»::«compname»(«Utils.printConfigurationParametersAsList(comp)»)
		«IF comp.isAtomic || !comp.parameters.isEmpty || !comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty].isEmpty»
		:
    	«ENDIF»
    	«IF comp.isAtomic»
			«printBehaviorInitializerListEntry(comp, compname)»
        «ENDIF»
    	«IF comp.isAtomic && !comp.parameters.isEmpty»,«ENDIF»
		«Subcomponents.printInitializerList(comp)»
		«IF !comp.parameters.isEmpty && !comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty].isEmpty»,«ENDIF»
		«IF comp.isAtomic && comp.parameters.isEmpty && !comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty].isEmpty»,«ENDIF»
		«FOR param : comp.parameters SEPARATOR ','»
      	«param.name» («param.name»)
    	«ENDFOR»
		{
			«IF comp.presentParentComponent»
			super(«FOR inhParam : getInheritedParams(comp) SEPARATOR ','» «inhParam» «ENDFOR»);
			«ENDIF»
		}
		'''
	}
	
	def static printBehaviorInitializerListEntry(ComponentTypeSymbol comp, String compname) {
		return '''
		«Identifier.behaviorImplName»(«compname»Impl«Utils.printFormalTypeParameters(comp, false)»(
		«IF comp.hasParameters»
	        «FOR param : comp.parameters SEPARATOR ','»
	          «param.name»
	        «ENDFOR»
    «ENDIF»
	))'''.toString().replace("\n", "")
	}
	
	def static printComputeAtomic(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::compute() {
			if (shouldCompute())
			{
				«printComputeInputs(comp, compname)»
				«compname»Result«Utils.printFormalTypeParameters(comp)» result;
				«FOR port: comp.incomingPorts»
        ««« «ValueCheck.printPortValuecheck(comp, port)»
        «ENDFOR»
				«printAssumptionsCheck(comp, compname)»
				result = «Identifier.behaviorImplName».compute(input);
				«FOR port: comp.outgoingPorts»
          ««« «ValueCheck.printPortValuecheck(comp, port)»
        «ENDFOR»
				«printGuaranteesCheck(comp, compname)»
				setResult(result);				
			}
		}
		'''
	}
	
	def static printComputeInputs(ComponentTypeSymbol comp, String compname) {
		return printComputeInputs(comp, compname, false);
	}
	
	def static printComputeInputs(ComponentTypeSymbol comp, String compname, boolean isMonitor) {
		return '''
		«IF !ComponentHelper.usesBatchMode(comp)»
		«compname»Input«Utils.printFormalTypeParameters(comp)» input«IF !comp.allIncomingPorts.empty»(«FOR inPort : comp.allIncomingPorts SEPARATOR ','»getPort«inPort.name.toFirstUpper»()->getCurrentValue(«IF isMonitor»portMonitorUuid«inPort.name.toFirstUpper»«ELSE»this->uuid«ENDIF»)«ENDFOR»)«ENDIF»;
		«ELSE»
		«compname»Input«Utils.printFormalTypeParameters(comp)» input;
		«FOR inPort : ComponentHelper.getPortsInBatchStatement(comp)»
		while(getPort«inPort.name.toFirstUpper»()->hasValue(this->uuid)){
			input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(«IF isMonitor»portMonitorUuid«inPort.name.toFirstUpper»«ELSE»this->uuid«ENDIF»));
		}
		«ENDFOR»
		«FOR inPort : ComponentHelper.getPortsNotInBatchStatements(comp)»
		input.add«inPort.name.toFirstUpper»Element(getPort«inPort.name.toFirstUpper»()->getCurrentValue(«IF isMonitor»portMonitorUuid«inPort.name.toFirstUpper»«ELSE»this->uuid«ENDIF»));
		«ENDFOR»
		«ENDIF»
		'''
	}
	
	def static printShouldComputeCheck(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		bool «compname»«Utils.printFormalTypeParameters(comp)»::shouldCompute() {
			«IF comp.allIncomingPorts.length > 0 && !ComponentHelper.hasSyncGroups(comp)»
			if (timeMode == TIMESYNC || «FOR inPort : comp.allIncomingPorts SEPARATOR ' || '»getPort«inPort.name.toFirstUpper»()->hasValue(this->uuid)«ENDFOR»)
			{ return true; }
			«ENDIF»
			«IF ComponentHelper.hasSyncGroups(comp)»
			if ( 
				«FOR syncGroup : ComponentHelper.getSyncGroups(comp)  SEPARATOR ' || '»
				(«FOR port : syncGroup SEPARATOR ' && '» getPort«port.toFirstUpper»()->hasValue(this->uuid) «ENDFOR»)
				«ENDFOR»
				«IF ComponentHelper.getPortsNotInSyncGroup(comp).length() > 0»
				|| «FOR port : ComponentHelper.getPortsNotInSyncGroup(comp) SEPARATOR ' || '» getPort«port.name.toFirstUpper»()->hasValue(this->uuid)«ENDFOR»
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
	
	def static printAssumptionsCheck(ComponentTypeSymbol comp, String compname) {
		var assumptions = ComponentHelper.getPreconditions(comp);
		return '''
		«FOR statement : assumptions»
		if (
		«FOR port : ComponentHelper.getPortsInGuardExpression(statement.guard) SEPARATOR ' && '»
			«IF !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.name)»
				input.get«port.name.toFirstUpper»()
			«ELSE»
				true // presence of value on port «port.name» not checked as it is compared to NoData
			«ENDIF»
		«ENDFOR» && 
		!(
			«Utils.printExpression(statement.guard)»
		)) {
			std::stringstream error;
			error << "Violated assumption «Utils.printExpression(statement.guard, false)» on component «comp.packageName».«compname»" << std::endl;
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
	
	def static printGuaranteesCheck(ComponentTypeSymbol comp, String compname) {
		var guarantees = ComponentHelper.getPostconditions(comp);
		return '''
		«FOR statement : guarantees»
		if (
		«FOR port : ComponentHelper.getPortsInGuardExpression(statement.guard) SEPARATOR ' && '»
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
			«Utils.printExpression(statement.guard)»
		)) {
			std::stringstream error;
			error << "Violated guarantee «Utils.printExpression(statement.guard, false)» on component «comp.packageName».«compname»" << std::endl;
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
	
	def static printComputeDecomposed(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::compute(){
			if (shouldCompute()) {
			
			«printComputeInputs(comp, compname)»
			«FOR port: comp.incomingPorts»
			««« «ValueCheck.printPortValuecheck(comp, port)»
			«ENDFOR»
			«printAssumptionsCheck(comp, compname)»
			
			«FOR subcomponent : comp.subComponents»
				this->«subcomponent.name».compute();
      «ENDFOR»

      «printComputeResults(comp, compname, true)»
      «FOR port: comp.outgoingPorts»
        ««« «ValueCheck.printPortValuecheck(comp, port)»
      «ENDFOR»
      «printGuaranteesCheck(comp, compname)»
			}
		}
		'''
	}
	
	def static printComputeResults(ComponentTypeSymbol comp, String compname, boolean isMonitor) {
		return '''
		«compname»Result«Utils.printFormalTypeParameters(comp)» result;
		«FOR outPort : comp.allOutgoingPorts»
		if (getPort«outPort.name.toFirstUpper»()->hasValue(«IF isMonitor»portMonitorUuid«outPort.name.toFirstUpper»«ELSE»this->uuid«ENDIF»)) {
			result.set«outPort.name.toFirstUpper»(getPort«outPort.name.toFirstUpper»()->getCurrentValue(«IF isMonitor»portMonitorUuid«outPort.name.toFirstUpper»«ELSE»this->uuid«ENDIF»).value());
		}
		«ENDFOR»
		'''
	}
	
	def static printStartDecomposed(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::start(){
			«IF ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)»
			threads.push_back(std::thread{&«compname»«Utils.printFormalTypeParameters(comp)»::run, this});
			«ELSE»
			«FOR subcomponent : comp.subComponents»
			this->«subcomponent.name».start();
	        «ENDFOR»
			«ENDIF»		
		}
		'''
	}
	
	def static printStartAtomic(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp)»::start(){
			threads.push_back(std::thread{&«compname»«Utils.printFormalTypeParameters(comp)»::run, this});
		}
		'''
	}
	
	def static printRun(ComponentTypeSymbol comp, String compname) {
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
	
	def protected static List<String> getInheritedParams(ComponentTypeSymbol component) {
    var List<String> result = new ArrayList;
    var List<FieldSymbol> configParameters = component.getParameters();
    if (component.isPresentParentComponent()) {
      var ComponentTypeSymbolLoader superCompReference = component.getParent();
      var List<FieldSymbol> superConfigParams = superCompReference.getLoadedSymbol()
      .getParameters();
      if (!configParameters.isEmpty()) {
        for (var i = 0; i < superConfigParams.size(); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }
}