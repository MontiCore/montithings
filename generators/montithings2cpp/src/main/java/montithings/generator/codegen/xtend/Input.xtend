package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Input {

	def static generateImplementationFile(ComponentSymbol comp, String compname) {
    return '''
    #include "«compname»Input.h"
    «IF !Utils.hasTypeParameters(comp)»
    «generateInputBody(comp, compname)»
    «ENDIF»
    '''
  }
	
	
    def static generateInputBody(ComponentSymbol comp, String compname) {
    var ComponentHelper helper = new ComponentHelper(comp)
    var isBatch = ComponentHelper.usesBatchMode(comp);
    
    return '''
«IF !isBatch»

«IF !comp.allIncomingPorts.empty»
«Utils.printTemplateArguments(comp)»
«compname»Input«Utils.printFormalTypeParameters(comp, false)»::«compname»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» tl::optional<«helper.getRealPortCppTypeString(port)»> «port.name» «ENDFOR»){
«IF comp.superComponent.present»
	super(«FOR port : comp.superComponent.get.allIncomingPorts» «port.name» «ENDFOR»);
«ENDIF»
«FOR port : comp.incomingPorts»
	this->«port.name» = std::move(«port.name»); 
«ENDFOR»
}
«ENDIF»
«ENDIF»  
«FOR port : ComponentHelper.getPortsInBatchStatement(comp)»
«Utils.printTemplateArguments(comp)»
std::vector<«helper.getRealPortCppTypeString(port)»> «compname»Input«Utils.printFormalTypeParameters(comp, false)»::get«port.name.toFirstUpper»(){
	return «port.name»;
}

«Utils.printTemplateArguments(comp)»
void «compname»Input«Utils.printFormalTypeParameters(comp, false)»::add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port)»> element){
	if (element){
		«port.name».push_back(element.value());
 	}
}
«ENDFOR»

«FOR port : ComponentHelper.getPortsNotInBatchStatements(comp)»
«Utils.printTemplateArguments(comp)»
tl::optional<«helper.getRealPortCppTypeString(port)»> «compname»Input«Utils.printFormalTypeParameters(comp, false)»::get«port.name.toFirstUpper»(){
	return «port.name»;
}

«Utils.printTemplateArguments(comp)»
void «compname»Input«Utils.printFormalTypeParameters(comp, false)»::add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port)»> element){
	this->«port.name» = std::move(element);
} 
«ENDFOR»

'''
  }
	
	def static generateInputHeader(ComponentSymbol comp, String compname) {
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

«Utils.printTemplateArguments(comp)»
class «compname»Input
«IF comp.superComponent.present» : 
	«Utils.printSuperClassFQ(comp)»Input
«IF comp.superComponent.get.hasFormalTypeParameters»<
«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
    «scTypeParams»
«ENDFOR»>«ENDIF»
«ENDIF»
{
private:
«FOR port : ComponentHelper.getPortsNotInBatchStatements(comp)»
	tl::optional<«helper.getRealPortCppTypeString(port)»> «port.name»;
«ENDFOR»
«FOR port : ComponentHelper.getPortsInBatchStatement(comp)»
	std::vector<«helper.getRealPortCppTypeString(port)»> «port.name» = {};
«ENDFOR»
public:

	«compname»Input() = default;
	«IF !comp.allIncomingPorts.empty && !isBatch»
	explicit «compname»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» tl::optional<«helper.getRealPortCppTypeString(port)»> «port.name» «ENDFOR»);
    «ENDIF»
	
	
	«FOR port : ComponentHelper.getPortsNotInBatchStatements(comp)»
	tl::optional<«helper.getRealPortCppTypeString(port)»> get«port.name.toFirstUpper»();
	void add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port)»>);
	«ENDFOR»
	«FOR port : ComponentHelper.getPortsInBatchStatement(comp)»
	std::vector<«helper.getRealPortCppTypeString(port)»> get«port.name.toFirstUpper»();
	void add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port)»>);
	«ENDFOR»
	

};

«IF Utils.hasTypeParameters(comp)»
  «generateInputBody(comp, compname)»
«ENDIF»

'''
	}
	
}