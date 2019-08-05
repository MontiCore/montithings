package de.montiarcautomaton.generator.codegen.xtend.behavior

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Utils

abstract class ABehaviorGenerator {

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the compute() method
   */
  def String printCompute(ComponentSymbol comp);

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the getInitialValues() method
   */
  def String printGetInitialValues(ComponentSymbol comp);

  /**
   * This method can be used to add additional code to the implementation class without.   
   */
  def String hook(ComponentSymbol comp);

  /**
   * Entry point for generating a component's implementation.
   * 
   */
  def String generateHeader(ComponentSymbol comp) {
  	var String generics = Utils.printFormalTypeParameters(comp)
    return '''
    #pragma once
    #include "«comp.name»Input.h"
    #include "«comp.name»Result.h"
    #include "IComputable.h"
    #include <stdexcept>
    «Utils.printCPPImports(comp)»
		
    class «comp.name»«generics»Impl : IComputable<«comp.name»Input«generics»,«comp.name»Result«generics»>{ {
	private:  
        «Utils.printVariables(comp)»
        «Utils.printConfigParameters(comp)»
		
        
    public:
      	«hook(comp)»
		«printConstructor(comp)»
		virtual «comp.name»Result getInitialValues() override;
		virtual «comp.name»Result compute(«comp.name»Input input) override;

    }
    '''
  }
  
  def String generateBody(ComponentSymbol comp){
  	return'''
  	#include "«comp.name»Impl.h"

    «printGetInitialValues(comp)»
    
    «printCompute(comp)»

  	
  	'''
  }
    


  def String printConstructor(ComponentSymbol comp) {
    return '''
       «comp.name»Impl(«Utils.printConfiurationParametersAsList(comp)») {
        «FOR param : comp.configParameters»
          this.«param.name» = «param.name»; 
        «ENDFOR»
      }
    '''

  }

}
