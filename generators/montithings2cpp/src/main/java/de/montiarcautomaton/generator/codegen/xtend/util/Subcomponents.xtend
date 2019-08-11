package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Subcomponents {

  def static String printVars(ComponentSymbol comp) {
  	var helper = new ComponentHelper(comp)
    return '''
      «FOR subcomponent : comp.subComponents»
        «var type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent)»
        «type» «subcomponent.name»«IF !helper.getParamValues(subcomponent).isEmpty»(
                «FOR param : helper.getParamValues(subcomponent) SEPARATOR ','»
                  «param»
                «ENDFOR»
                )«ENDIF»;
      «ENDFOR»
    '''
  }
  

	
}
