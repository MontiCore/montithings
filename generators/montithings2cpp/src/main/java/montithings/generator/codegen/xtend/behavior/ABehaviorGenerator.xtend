// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.behavior

import montiarc._symboltable.ComponentSymbol
import montithings.generator.codegen.xtend.util.Utils

abstract class ABehaviorGenerator {

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the compute() method
   */
  def String printCompute(ComponentSymbol comp, String compname);

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the getInitialValues() method
   */
  def String printGetInitialValues(ComponentSymbol comp, String compname);

  /**
   * This method can be used to add additional code to the implementation class without.   
   */
  def String hook(ComponentSymbol comp, String compname);

  /**
   * Entry point for generating a component's implementation.
   * 
   */
  def String generateHeader(ComponentSymbol comp, String compname) {
  	var String generics = Utils.printFormalTypeParameters(comp, false)
    return '''
#pragma once
#include "«compname»Input.h"
#include "«compname»Result.h"
#include "IComputable.h"
#include <stdexcept>
«Utils.printCPPImports(comp)»

«Utils.printTemplateArguments(comp)»
class «compname»Impl : IComputable<«compname»Input«generics»,«compname»Result«generics»>{ {
private:  
    «Utils.printVariables(comp)»
    «Utils.printConfigParameters(comp)»
	
    
public:
  	«hook(comp, compname)»
	«printConstructor(comp, compname)»
	virtual «compname»Result«generics» getInitialValues() override;
	virtual «compname»Result«generics» compute(«compname»Input«generics» input) override;

    };
    
    «IF Utils.hasTypeParameters(comp)»
    	«generateBody(comp, compname)»
	«ENDIF»
    '''
  }
  
  def String generateImplementationFile(ComponentSymbol comp, String compname) {
	  return '''
	#include "«compname»Impl.h"
	«IF !Utils.hasTypeParameters(comp)»
	«generateBody(comp, compname)»
	«ENDIF»
	'''
  }
  
  def String generateBody(ComponentSymbol comp, String compname){
  	return'''
«printGetInitialValues(comp, compname)»

«printCompute(comp, compname)»
'''
  }
    


  def String printConstructor(ComponentSymbol comp, String compname) {
    return '''
«compname»Impl(«Utils.printConfigurationParametersAsList(comp)»)
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
