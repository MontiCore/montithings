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

class «compname»«generics»Impl : IComputable<«compname»Input«generics»,«compname»Result«generics»>{
	
private:  
    «Utils.printVariables(comp)»
    «Utils.printConfigParameters(comp)»
    
public:
    «printConstructor(comp)»
	//«compname»«generics»Impl() = default;
	«compname»Result getInitialValues() override;
	«compname»Result compute(«compname»Input input) override;
};
'''
  }
  
  	def static generateAbstractAtomicImplementationBody(ComponentSymbol comp, String compname) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
#include "«compname»Impl.h"

«compname»Result «compname»Impl::getInitialValues(){
	throw std::runtime_error("Invoking getInitialValues() on abstract implementation «comp.packageName».«compname»");
}

«compname»Result «compname»Impl::compute(«compname»Input«generics» «Identifier.inputName»){
	throw std::runtime_error("Invoking compute() on abstract implementation «comp.packageName».«compname»");  	
}
'''
  }
  
    def static String printConstructor(ComponentSymbol comp) {
    return '''
«comp.name»Impl(«Utils.printConfigurationParametersAsList(comp)») {
«FOR param : comp.configParameters»
	this.«param.name» = «param.name»; 
«ENDFOR»
}
'''

  }
	
}