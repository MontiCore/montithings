package de.montiarcautomaton.generator.codegen.xtend

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Ports
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.codegen.xtend.util.Subcomponents
import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.montiarcautomaton.generator.codegen.xtend.util.Setup
import de.montiarcautomaton.generator.codegen.xtend.util.Init
import de.montiarcautomaton.generator.codegen.xtend.util.Update
import java.util.List
import java.util.ArrayList
import de.monticore.symboltable.types.JFieldSymbol
import montiarc._symboltable.ComponentSymbolReference

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
			«Ports.printVars(comp.ports)»
			«Ports.printResourcePortVars(ComponentHelper.getResourcePortsInComponent(comp))»
			«Utils.printVariables(comp)»
			«Utils.printConfigParameters(comp)»
			«IF comp.isDecomposed»
			«Subcomponents.printVars(comp)»
			«ELSE»
			«comp.name»Impl «Identifier.behaviorImplName»;
			void initialize();
			void setResult(«comp.name»Result result);
			«ENDIF»
			
			
		public:
			«Ports.printMethodHeaders(comp.ports)»
			«IF comp.isDecomposed»	
			«Subcomponents.printMethodHeaders(comp)»
			«ENDIF»
			
			«comp.name»(«Utils.printConfiurationParametersAsList(comp)»);
			
			void setUp() override;
			void init() override;
			void compute() override;
			void update() override;
			
		};            
		            
		            
		
		'''
	}
	
	def static generateBody(ComponentSymbol comp) {
		return '''
		#include "«comp.name».h"
		
		«Ports.printMethodBodies(comp.ports, comp)»
		
		«IF comp.isDecomposed»
		«printComputeDecomposed(comp)»
		«Subcomponents.printMethodBodies(comp)»
		«ELSE»
		«printComputeAtomic(comp)»
		
		void «comp.name»::initialize(){
			«comp.name»Result result = «Identifier.behaviorImplName».getInitialValues();
			setResult(result);
			update();
		}
		
		void «comp.name»::setResult(«comp.name»Result result){
			«FOR portOut : comp.outgoingPorts»
            this->getPort«portOut.name.toFirstUpper»()->setNextValue(result.get«portOut.name.toFirstUpper»());
            «ENDFOR»
			
		}
		
		
		«ENDIF»
		«Setup.print(comp)»
		
		«Init.print(comp)»
		
		«Update.print(comp)»
		
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
			«comp.name»Input input«IF !comp.allIncomingPorts.empty»(«FOR inPort : comp.allIncomingPorts SEPARATOR ','»getPort«inPort.name.toFirstUpper»()->getCurrentValue()«ENDFOR»)«ENDIF»;
			
			«comp.name»Result result = «Identifier.behaviorImplName».compute(input);
			setResult(result);				
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
	
	
}