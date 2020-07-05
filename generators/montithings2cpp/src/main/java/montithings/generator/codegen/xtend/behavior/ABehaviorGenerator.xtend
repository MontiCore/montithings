// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.behavior

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils

abstract class ABehaviorGenerator {

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the compute() method
   */
  def String printCompute(ComponentTypeSymbol comp, String compname);

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the getInitialValues() method
   */
  def String printGetInitialValues(ComponentTypeSymbol comp, String compname);

  /**
   * This method can be used to add additional code to the implementation class without.   
   */
  def String hook(ComponentTypeSymbol comp, String compname);

  /**
   * Entry point for generating a component's implementation.
   * 
   */
  def String generateHeader(ComponentTypeSymbol comp, String compname) {
  	var String generics = Utils.printFormalTypeParameters(comp, false)
    return '''
#pragma once
#include "«compname»Input.h"
#include "«compname»Result.h"
#include "IComputable.h"
#include <stdexcept>
«Utils.printNamespaceStart(comp)»

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
    
    «IF comp.hasTypeParameter()»
    	«generateBody(comp, compname)»
	«ENDIF»
«Utils.printNamespaceEnd(comp)»
    '''
  }
  
  def String generateImplementationFile(ComponentTypeSymbol comp, String compname) {
	  return '''
	#include "«compname»Impl.h"
	«Utils.printNamespaceStart(comp)»
	«IF !comp.hasTypeParameter()»
	«generateBody(comp, compname)»
	«ENDIF»
	«Utils.printNamespaceEnd(comp)»
	'''
  }
  
  def String generateBody(ComponentTypeSymbol comp, String compname){
  	return'''
«printGetInitialValues(comp, compname)»

«printCompute(comp, compname)»
'''
  }
    


  def String printConstructor(ComponentTypeSymbol comp, String compname) {
    return '''
«compname»Impl(«Utils.printConfigurationParametersAsList(comp)»)
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
