// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montithings._symboltable.ComponentSymbol

class Result {

  def static generateImplementationFile(ComponentSymbol comp, String compname) {
    return '''
    #include "«compname»Result.h"
    «IF !Utils.hasTypeParameters(comp)»
    «generateResultBody(comp, compname)»
    «ENDIF»
    '''
  }


	def static generateResultBody(ComponentSymbol comp, String compname){
		var ComponentHelper helper = new ComponentHelper(comp)
	    return '''
«IF !comp.allOutgoingPorts.empty»
«Utils.printTemplateArguments(comp)»
«compname»Result«Utils.printFormalTypeParameters(comp, false)»::«compname»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port)» «port.name» «ENDFOR»){
	«IF comp.superComponent.present»
	super(«FOR port : comp.superComponent.get.allOutgoingPorts» «port.name» «ENDFOR»);
«ENDIF»
«FOR port : comp.outgoingPorts»
	  this->«port.name» = «port.name»; 
«ENDFOR»
}
«ENDIF»

«FOR port : comp.outgoingPorts»
«Utils.printTemplateArguments(comp)»
tl::optional<«helper.getRealPortCppTypeString(port)»> «compname»Result«Utils.printFormalTypeParameters(comp, false)»::get«port.name.toFirstUpper»(){
	return «port.name»;
 }
 «ENDFOR»
 
 «FOR port : comp.outgoingPorts»
«Utils.printTemplateArguments(comp)»
void «compname»Result«Utils.printFormalTypeParameters(comp, false)»::set«port.name.toFirstUpper»(«helper.getRealPortCppTypeString(port)» «port.name»){
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

«Utils.printTemplateArguments(comp)»
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

«IF Utils.hasTypeParameters(comp)»
  «generateResultBody(comp, compname)»
«ENDIF»
'''
		
	}
}