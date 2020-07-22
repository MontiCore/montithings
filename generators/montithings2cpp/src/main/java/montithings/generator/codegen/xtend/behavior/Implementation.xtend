// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.behavior

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.helper.ComponentHelper

class Implementation {
	def static generateImplementationHeader(ComponentTypeSymbol comp, String compname, boolean existsHWC) {
    var String generics = Utils.printFormalTypeParameters(comp);
    return '''
#pragma once
#include "«compname»Input.h"
#include "«compname»Result.h"
#include "IComputable.h"
#include <stdexcept>
«Utils.printNamespaceStart(comp)»

«Utils.printTemplateArguments(comp)»
class «compname»Impl«IF existsHWC»TOP«ENDIF» : public IComputable<«compname»Input«generics»,«compname»Result«generics»>{
	
protected:  
    «Utils.printVariables(comp)»
    ««« Currently useless. MontiArc 6's getFields() returns both variables and parameters
    ««« «Utils.printConfigParameters(comp)»
    
public:
  «printConstructor(comp, existsHWC)»

  «IF ComponentHelper.hasBehavior(comp)»
	«compname»Result«generics» getInitialValues() override;
	«compname»Result«generics» compute(«compname»Input«generics» input) override;
  «ELSE»
  «compname»Result«generics» getInitialValues() = 0;
  «compname»Result«generics» compute(«compname»Input«generics» input) = 0;
  «ENDIF»
};

«IF comp.hasTypeParameter»
	«generateImplementationBody(comp, compname, existsHWC)»
«ENDIF»
«Utils.printNamespaceEnd(comp)»
'''
  }
  
  def static String generateImplementationFile(ComponentTypeSymbol comp, String compname, boolean existsHWC) {
	  return '''
	#include "«compname»Impl«IF existsHWC»TOP«ENDIF».h"
	«Utils.printNamespaceStart(comp)»
	«IF !comp.hasTypeParameter»
	«generateImplementationBody(comp, compname, existsHWC)»
	«ENDIF»
	«Utils.printNamespaceEnd(comp)»
	'''
  }
  
  	def static generateImplementationBody(ComponentTypeSymbol comp, String compname, boolean isTOP) {
    var String generics = Utils.printFormalTypeParameters(comp);
    return '''
«IF ComponentHelper.hasBehavior(comp)»
«Utils.printTemplateArguments(comp)»
«compname»Result«generics» «compname»Impl«IF isTOP»TOP«ENDIF»«generics»::getInitialValues(){
	return {};
}

«Utils.printTemplateArguments(comp)»
«compname»Result«generics» «compname»Impl«IF isTOP»TOP«ENDIF»«generics»::compute(«compname»Input«generics» «Identifier.inputName»){
  «compname»Result«generics» result;
	«ComponentHelper.printStatementBehavior(comp)»
  return result;
}
«ENDIF»
'''
  }
  
    def static String printConstructor(ComponentTypeSymbol comp, boolean isTOP) {
    return '''
«comp.name»Impl«IF isTOP»TOP«ENDIF»(«Utils.printConfigurationParametersAsList(comp)»)
«IF !comp.parameters.isEmpty»
:
«FOR param : comp.parameters SEPARATOR ','»
    «param.name» («param.name»)
«ENDFOR»
{
}
«ELSE»
= default;
«ENDIF»
'''

  }
	
}