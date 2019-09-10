package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Result {
	def static generateResultBody(ComponentSymbol comp){
		var ComponentHelper helper = new ComponentHelper(comp)
	    return '''
#include "«comp.name»Result.h"

«IF !comp.allOutgoingPorts.empty»
«comp.name»Result::«comp.name»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port)» «port.name» «ENDFOR»){
	«IF comp.superComponent.present»
	super(«FOR port : comp.superComponent.get.allOutgoingPorts» «port.name» «ENDFOR»);
«ENDIF»
«FOR port : comp.outgoingPorts»
	  this->«port.name» = «port.name»; 
«ENDFOR»
}
«ENDIF»

«FOR port : comp.outgoingPorts»
tl::optional<«helper.getRealPortCppTypeString(port)»> «comp.name»Result::get«port.name.toFirstUpper»(){
	return «port.name»;
 }
 «ENDFOR»
 
 «FOR port : comp.outgoingPorts»
void «comp.name»Result::set«port.name.toFirstUpper»(«helper.getRealPortCppTypeString(port)» «port.name»){
		this->«port.name» = «port.name»; 
 }
 «ENDFOR»
	    '''
	}
	
	def static generateResultHeader(ComponentSymbol comp){
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

class «comp.name»Result
  «IF comp.superComponent.present» : 
    «Utils.printSuperClassFQ(comp)»Result
    «IF comp.superComponent.get.hasFormalTypeParameters»<
    «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
        «scTypeParams»
        «ENDFOR» > «ENDIF»
    «ENDIF»
{
private:
	«FOR port : comp.outgoingPorts»
	tl::optional<«helper.getRealPortCppTypeString(port)»> «port.name»;
	«ENDFOR»

public:	
	«comp.name»Result() = default;
	«IF !comp.allOutgoingPorts.empty»
	«comp.name»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port)» «port.name» «ENDFOR»);
	«ENDIF»
	
	«FOR port : comp.outgoingPorts»
	 tl::optional<«helper.getRealPortCppTypeString(port)»> get«port.name.toFirstUpper»();
	«ENDFOR»
	
	«FOR port : comp.outgoingPorts»
	 void set«port.name.toFirstUpper»(«helper.getRealPortCppTypeString(port)» «port.name»);
	«ENDFOR»
};
'''
		
	}
}