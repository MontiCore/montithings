package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Result {
	def static generateResultBody(ComponentSymbol comp, String compname){
		var ComponentHelper helper = new ComponentHelper(comp)
	    return '''
#include "«compname»Result.h"

«IF !comp.allOutgoingPorts.empty»
«compname»Result::«compname»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port)» «port.name» «ENDFOR»){
	«IF comp.superComponent.present»
	super(«FOR port : comp.superComponent.get.allOutgoingPorts» «port.name» «ENDFOR»);
«ENDIF»
«FOR port : comp.outgoingPorts»
	  this->«port.name» = «port.name»; 
«ENDFOR»
}
«ENDIF»

«FOR port : comp.outgoingPorts»
tl::optional<«helper.getRealPortCppTypeString(port)»> «compname»Result::get«port.name.toFirstUpper»(){
	return «port.name»;
 }
 «ENDFOR»
 
 «FOR port : comp.outgoingPorts»
void «compname»Result::set«port.name.toFirstUpper»(«helper.getRealPortCppTypeString(port)» «port.name»){
		this->«port.name» = «port.name»; 
 }
 «ENDFOR»
	    '''
	}
	
	def static generateResultHeader(ComponentSymbol comp, String compname){
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

class «compname»Result
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
	«compname»Result() = default;
	«IF !comp.allOutgoingPorts.empty»
	«compname»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port)» «port.name» «ENDFOR»);
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