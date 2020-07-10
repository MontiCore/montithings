// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.behavior

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier

class AtomicComponentStandardImplementation {
	def static generateAbstractAtomicImplementationHeader(ComponentTypeSymbol comp, String compname) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
#pragma once
#include "«compname»Input.h"
#include "«compname»Result.h"
#include "IComputable.h"
#include <stdexcept>
«Utils.printNamespaceStart(comp)»

«Utils.printTemplateArguments(comp)»
class «compname»Impl : IComputable<«compname»Input«generics»,«compname»Result«generics»>{
	
private:  
    «Utils.printVariables(comp)»
    «Utils.printConfigParameters(comp)»
    
public:
    «printConstructor(comp)»
	//«compname»Impl() = default;
	«compname»Result«generics» getInitialValues() override;
	«compname»Result«generics» compute(«compname»Input«generics» input) override;
};

«IF comp.hasTypeParameter»
	«generateAbstractAtomicImplementationBody(comp, compname)»
«ENDIF»
«Utils.printNamespaceEnd(comp)»
'''
  }
  
  def static String generateImplementationFile(ComponentTypeSymbol comp, String compname) {
	  return '''
	#include "«compname»Impl.h"
	«Utils.printNamespaceStart(comp)»
	«IF !comp.hasTypeParameter»
	«generateAbstractAtomicImplementationBody(comp, compname)»
	«ENDIF»
	«Utils.printNamespaceEnd(comp)»
	'''
  }
  
  	def static generateAbstractAtomicImplementationBody(ComponentTypeSymbol comp, String compname) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
«Utils.printTemplateArguments(comp)»
«compname»Result«generics» «compname»Impl«generics»::getInitialValues(){
	throw std::runtime_error("Invoking getInitialValues() on abstract implementation «comp.packageName».«compname»");
}

// TODO: Replace compute method by code generated from MCStatements
«Utils.printTemplateArguments(comp)»
«compname»Result«generics» «compname»Impl«generics»::compute(«compname»Input«generics» «Identifier.inputName»){
	throw std::runtime_error("Invoking compute() on abstract implementation «comp.packageName».«compname»");  	
}
'''
  }
  
    def static String printConstructor(ComponentTypeSymbol comp) {
    return '''
«comp.name»Impl(«Utils.printConfigurationParametersAsList(comp)»)
«IF !comp.parameters.isEmpty»
:
«ENDIF»
«FOR param : comp.parameters SEPARATOR ','»
    «param.name» («param.name»)
«ENDFOR»
{
}
'''

  }
	
}