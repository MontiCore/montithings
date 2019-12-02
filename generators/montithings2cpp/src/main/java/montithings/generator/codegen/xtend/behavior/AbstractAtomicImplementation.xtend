package montithings.generator.codegen.xtend.behavior

import montiarc._symboltable.ComponentSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier

class AbstractAtomicImplementation {
	def static generateAbstractAtomicImplementationHeader(ComponentSymbol comp) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
#pragma once
#include "«comp.name»Input.h"
#include "«comp.name»Result.h"
#include "IComputable.h"
#include <stdexcept>
«Utils.printCPPImports(comp)»

class «comp.name»«generics»Impl : IComputable<«comp.name»Input«generics»,«comp.name»Result«generics»>{
private:  
    «Utils.printVariables(comp)»
    «Utils.printConfigParameters(comp)»
	
public:
	«printConstructor(comp)»
	«comp.name»«generics»Impl() = default;
	«comp.name»Result getInitialValues() override;
	«comp.name»Result compute(«comp.name»Input input) override;
};
'''
  }
  
  	def static generateAbstractAtomicImplementationBody(ComponentSymbol comp) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
#include "«comp.name»Impl.h"

«comp.name»Result «comp.name»Impl::getInitialValues(){
	throw std::runtime_error("Invoking getInitialValues() on abstract implementation «comp.packageName».«comp.name»");
}

«comp.name»Result «comp.name»Impl::compute(«comp.name»Input«generics» «Identifier.inputName»){
	throw std::runtime_error("Invoking compute() on abstract implementation «comp.packageName».«comp.name»");  	
}
'''
  }
  
    def static String printConstructor(ComponentSymbol comp) {
    return '''
«comp.name»Impl(«Utils.printConfiurationParametersAsList(comp)») {
«FOR param : comp.configParameters»
	this.«param.name» = «param.name»; 
«ENDFOR»
}
'''

  }
	
}