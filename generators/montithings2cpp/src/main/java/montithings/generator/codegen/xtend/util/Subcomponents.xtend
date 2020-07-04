// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.ComponentInstanceSymbol
import java.util.HashMap
import java.util.HashSet
import java.util.Set

class Subcomponents {
	
  def static String printIncludes(ComponentTypeSymbol comp, String compname, HashMap<String, String> interfaceToImplementation) {
  	var Set<String> compIncludes = new HashSet<String>()
    for (subcomponent : comp.subComponents) {
      compIncludes.add('''#include "«ComponentHelper.getPackagePath(comp, subcomponent)»«ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, interfaceToImplementation, false)».h"''')
	  var Set<String> genericIncludes = ComponentHelper.includeGenericComponent(comp, subcomponent)
	  for (String genericInclude : genericIncludes) {
	    compIncludes.add('''#include "«genericInclude».h"''')
	  }
	}
	return '''
	«FOR include : compIncludes»
	«include»
	«ENDFOR»
	#include "«compname»Input.h"
	#include "«compname»Result.h"
	'''
  }

  def static String printVars(ComponentTypeSymbol comp, HashMap<String, String> interfaceToImplementation) {
    return '''
      «FOR subcomponent : comp.subComponents»
        «var type = ComponentHelper.getSubComponentTypeNameWithBinding(comp, subcomponent, interfaceToImplementation)»
        «printPackageNamespace(comp, subcomponent)»«type» «subcomponent.name»;
      «ENDFOR»
    '''
  }


  def static String printInitializerList(ComponentTypeSymbol comp) {
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
  
  def static String printPackageNamespace(ComponentTypeSymbol comp, ComponentInstanceSymbol subcomp) {
  	var subcomponentType = subcomp.typeInfo
  	var fullNamespaceSubcomponent = ComponentHelper.printPackageNamespaceForComponent(subcomponentType)
  	var fullNamespaceEnclosingComponent = ComponentHelper.printPackageNamespaceForComponent(comp)
  	if (!fullNamespaceSubcomponent.equals(fullNamespaceEnclosingComponent) && 
  		fullNamespaceSubcomponent.startsWith(fullNamespaceEnclosingComponent)) {
  		return fullNamespaceSubcomponent.split(fullNamespaceEnclosingComponent).get(1)
  	} else {
  		return fullNamespaceSubcomponent
  	}
  }
}
