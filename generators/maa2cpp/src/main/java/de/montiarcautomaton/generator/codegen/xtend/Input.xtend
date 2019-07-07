package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Input {
	
	
	
    def static generateInputBody(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    
    return '''
      #include "«comp.name»Input.h"      
      
      «IF !comp.allIncomingPorts.empty»
      	«comp.name»Input::«comp.name»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port)» «port.name» «ENDFOR»){
      		«IF comp.superComponent.present»
      		  super(«FOR port : comp.superComponent.get.allIncomingPorts» «port.name» «ENDFOR»);
      		«ENDIF»
      		«FOR port : comp.incomingPorts»
      		  this->«port.name» = «port.name»; 
      		«ENDFOR»
      	}
      «ENDIF»
      
      «FOR port : comp.incomingPorts»
      	 «helper.getRealPortCppTypeString(port)» «comp.name»Input::get«port.name.toFirstUpper»(){
      	 	return «port.name»;
      	 }
      				«ENDFOR»
      
    '''
  }
	
	def static generateInputHeader(ComponentSymbol comp) {
	var ComponentHelper helper = new ComponentHelper(comp)
		
		return '''
			#pragma once
			#include <string>
			#include "Port.h"
			#include <string>
			#include <map>
			#include <vector>
			#include <list>
			#include <set>
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
				«helper.getRealPortCppTypeString(port)» «port.name»;
			«ENDFOR»
			public:
				«comp.name»Input() {};
				«IF !comp.allIncomingPorts.empty»
			    «comp.name»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port)» «port.name» «ENDFOR»);
			    «ENDIF»
				
				«FOR port : comp.incomingPorts»
			    «helper.getRealPortCppTypeString(port)» get«port.name.toFirstUpper»();
				«ENDFOR»
			};
			
	
		'''
	}
	
}