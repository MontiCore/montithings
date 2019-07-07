package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol

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
	
	public:
		«comp.name»«generics»Impl(){};
		virtual «comp.name»Result getInitialValues() override;
		virtual «comp.name»Result compute(«comp.name»Input input) override;
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
	
}