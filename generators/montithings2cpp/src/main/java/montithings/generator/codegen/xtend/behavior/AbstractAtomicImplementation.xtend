// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.behavior

import montiarc._symboltable.ComponentSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier

class AbstractAtomicImplementation {
	def static generateAbstractAtomicImplementationHeader(ComponentSymbol comp, String compname) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
#pragma once
#include "«compname»Input.h"
#include "«compname»Result.h"
#include "IComputable.h"
#include <stdexcept>
«Utils.printCPPImports(comp)»

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

«IF Utils.hasTypeParameters(comp)»
	«generateAbstractAtomicImplementationBody(comp, compname)»
«ENDIF»
'''
  }
  
  def static String generateImplementationFile(ComponentSymbol comp, String compname) {
	  return '''
	#include "«compname»Impl.h"
	«IF !Utils.hasTypeParameters(comp)»
	«generateAbstractAtomicImplementationBody(comp, compname)»
	«ENDIF»
	'''
  }
  
  	def static generateAbstractAtomicImplementationBody(ComponentSymbol comp, String compname) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
«Utils.printTemplateArguments(comp)»
«compname»Result«generics» «compname»Impl«generics»::getInitialValues(){
	throw std::runtime_error("Invoking getInitialValues() on abstract implementation «comp.packageName».«compname»");
}

«Utils.printTemplateArguments(comp)»
«compname»Result«generics» «compname»Impl«generics»::compute(«compname»Input«generics» «Identifier.inputName»){
	throw std::runtime_error("Invoking compute() on abstract implementation «comp.packageName».«compname»");  	
}
'''
  }
  
    def static String printConstructor(ComponentSymbol comp) {
    return '''
«comp.name»Impl(«Utils.printConfigurationParametersAsList(comp)»)
«IF !comp.configParameters.isEmpty»
:
«ENDIF»
«FOR param : comp.configParameters SEPARATOR ','»
    «param.name» («param.name»)
«ENDFOR»
{
}
'''

  }
	
}