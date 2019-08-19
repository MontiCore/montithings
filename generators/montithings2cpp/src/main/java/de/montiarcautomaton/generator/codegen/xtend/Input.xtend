package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Input {
	
	
	
    def static generateInputBody(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    var isBatch = ComponentHelper.usesBatchMode(comp);
    
    return '''
      #include "«comp.name»Input.h"      
      «IF !isBatch»
      «IF !comp.allIncomingPorts.empty»
      	«comp.name»Input::«comp.name»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» tl::optional<«helper.getRealPortCppTypeString(port)»> «port.name» «ENDFOR»){
      		«IF comp.superComponent.present»
      		  super(«FOR port : comp.superComponent.get.allIncomingPorts» «port.name» «ENDFOR»);
      		«ENDIF»
      		«FOR port : comp.incomingPorts»
      		  this->«port.name» = std::move(«port.name»); 
      		«ENDFOR»
      	}
      «ENDIF»
      «ENDIF»
      
      «FOR port : comp.incomingPorts»
      «IF !isBatch»
      	 tl::optional<«helper.getRealPortCppTypeString(port)»> «comp.name»Input::get«port.name.toFirstUpper»(){
      	 	return «port.name»;
      	 }
      «ELSE»	 
      	 std::vector<«helper.getRealPortCppTypeString(port)»> «comp.name»Input::get«port.name.toFirstUpper»(){
      	       	 	return «port.name»;
      	       	 }
      	 
      	 void «comp.name»Input::add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port)»> element){
      	 	if (element){
      	 		«port.name».push_back(element.value());
      	 	}
      	 }
      «ENDIF»
      «ENDFOR»
      
    '''
  }
	
	def static generateInputHeader(ComponentSymbol comp) {
	var ComponentHelper helper = new ComponentHelper(comp)
	var isBatch = ComponentHelper.usesBatchMode(comp);
		
		return '''
			#pragma once
			#include <string>
			#include "Port.h"
			#include <string>
			#include <map>
			#include <vector>
			#include <list>
			#include <set>
			#include <utility>
			#include "tl/optional.hpp"
			«Utils.printCPPImports(comp)»
			
			class «comp.name»Input
			      «IF comp.superComponent.present» : 
			            «Utils.printSuperClassFQ(comp)»Input
			            «IF comp.superComponent.get.hasFormalTypeParameters»<
			            «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
			                «scTypeParams»
			                «ENDFOR» > «ENDIF»
			            «ENDIF»
			{
			private:
			«FOR port : comp.incomingPorts»
			«IF !isBatch»
				tl::optional<«helper.getRealPortCppTypeString(port)»> «port.name»;
			«ELSE»	
				std::vector<«helper.getRealPortCppTypeString(port)»> «port.name» = {};
			«ENDIF»
			«ENDFOR»
			public:
		
				«comp.name»Input() = default;
				«IF !comp.allIncomingPorts.empty && !isBatch»
			    explicit «comp.name»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» tl::optional<«helper.getRealPortCppTypeString(port)»> «port.name» «ENDFOR»);
			    «ENDIF»
				
				«FOR port : comp.incomingPorts»
				«IF !isBatch»
			    tl::optional<«helper.getRealPortCppTypeString(port)»> get«port.name.toFirstUpper»();
			    «ELSE»
			    std::vector<«helper.getRealPortCppTypeString(port)»> get«port.name.toFirstUpper»();
			    void add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port)»>);
			    «ENDIF»
				«ENDFOR»

			};
			
	
		'''
	}
	
}