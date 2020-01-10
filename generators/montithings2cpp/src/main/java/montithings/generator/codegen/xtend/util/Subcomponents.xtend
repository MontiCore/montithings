package montithings.generator.codegen.xtend.util

import montithings.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
import java.util.HashMap

class Subcomponents {

  def static String printVars(ComponentSymbol comp, HashMap<String, String> interfaceToImplementation) {
  	var helper = new ComponentHelper(comp)
    return '''
      «FOR subcomponent : comp.subComponents»
        «var type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, interfaceToImplementation)»
        «type» «subcomponent.name»;
      «ENDFOR»
    '''
  }


  def static String printInitializerList(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
      «FOR subcomponent : comp.subComponents.filter[x | !(new ComponentHelper(comp)).getParamValues(x).isEmpty] SEPARATOR ','»
        «subcomponent.name»(
          «FOR param : helper.getParamValues(subcomponent) SEPARATOR ','»
            «param»
          «ENDFOR»)
      «ENDFOR»
    '''
  }
}
