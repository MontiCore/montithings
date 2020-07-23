// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._ast.ASTPortAccess
import de.monticore.expressions.expressionsbasis._ast.ASTExpression
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol
import java.util.ArrayList
import java.util.List
import montithings.generator.helper.ComponentHelper
import montithings.generator.helper.CppPrettyPrinter

class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  def static printConfigurationParametersAsList(ComponentTypeSymbol comp) {
    return '''
      «FOR param : comp.parameters SEPARATOR ','»
        «param.type.print» «param.name»
      «ENDFOR»
    '''.toString().replace("\n", "")
  }

  /**
   * Prints the component's imports
   */
  def static printImports(ComponentTypeSymbol comp) {
    return '''
      «FOR _import : ComponentHelper.getImports(comp)»
        import «_import.statement»«IF _import.isStar».*«ENDIF»;
      «ENDFOR»
      «FOR inner : comp.innerComponents»
        import «printPackageWithoutKeyWordAndSemicolon(inner) + "." + inner.name»;
      «ENDFOR»
    '''
  }

  /**
   * Prints a member of given visibility name and type
   */
  def static printMember(String type, String name) {
    return '''
      «type» «name»;
    '''
  }
  
  /**
   * Prints members for configuration parameters.
   */
  def static printConfigParameters(ComponentTypeSymbol comp) {
    return '''
      «FOR param : comp.parameters»
        «printMember(ComponentHelper.printCPPTypeName(param.type), param.name)»
      «ENDFOR»
    '''.toString().replace("\n", "")
  }

  /**
   * Prints members for variables
   */
  def static printVariables(ComponentTypeSymbol comp) {
    return '''
      «FOR variable : ComponentHelper.getFields(comp)»
        «printMember(ComponentHelper.printCPPTypeName(variable.type), variable.name)»
      «ENDFOR»
    '''
  }

  /**
   * Prints formal parameters of a component.
   */
  def static printFormalTypeParameters(ComponentTypeSymbol comp) {
    return printFormalTypeParameters(comp, false)
  }
  def static printFormalTypeParameters(ComponentTypeSymbol comp, Boolean withClassPrefix) {
    return '''
      «IF comp.hasTypeParameter»
        <
          «FOR generic : getGenericParameters(comp) SEPARATOR ','»
            «IF withClassPrefix»class «ENDIF»«generic»
          «ENDFOR»
        >
      «ENDIF»
    '''.toString().replace("\n", "")
  }

  def static String printTemplateArguments(ComponentTypeSymbol comp) {
    return '''
    «IF comp.hasTypeParameter»
      template«Utils.printFormalTypeParameters(comp, true)»
    «ENDIF»
    '''.toString().replace("\n", "")
  }

  def private static List<String> getGenericParameters(ComponentTypeSymbol comp) {
    var List<String> output = new ArrayList
    if (comp.hasTypeParameter()) {
      var List<TypeVarSymbol> parameterList = comp.getTypeParameters()
      for (TypeVarSymbol typeParameter : parameterList) {
        output.add(typeParameter.getName())
      }
    }
    return output;
  }
  
  /**
   * Print the package declaration for generated component classes.
   * Uses recursive determination of the package name to accomodate for components
   * with at least two levels of inner component. These require changing the package name
   * to avoid name clashes between the generated packages and the outermost component.
   */
  def static String printPackage(ComponentTypeSymbol comp) {
  	return '''
  	«IF comp.isInnerComponent»
  	package «printPackageWithoutKeyWordAndSemicolon(comp.outerComponent.get) + "." + comp.outerComponent.get.name + "gen"»;
	«ELSE»
  	package «comp.packageName»;
	«ENDIF»
  	'''
  }
  
  /**
   * Helper function used to determine package names.
   */
  def static String printPackageWithoutKeyWordAndSemicolon(arcbasis._symboltable.ComponentTypeSymbol comp){
  	return '''
  	«IF comp.isInnerComponent»
  	«printPackageWithoutKeyWordAndSemicolon(comp.outerComponent.get) + "." + comp.outerComponent.get.name + "gen"»
	«ELSE»
  	«comp.packageName»
	«ENDIF»
  	'''
  }
  
  def static String printSuperClassFQ(ComponentTypeSymbol comp){
  	var String packageName = printPackageWithoutKeyWordAndSemicolon(comp.parentInfo);
  	if(packageName.equals("")){
  		return '''«comp.parent.name»'''
  	} else {
  		return '''«packageName».«comp.parent.name»'''
  	}
  }
  
  def static String printNamespaceStart(ComponentTypeSymbol comp) {
  	var packages = ComponentHelper.getPackages(comp);
  	return '''
  	namespace montithings {
  	«FOR i : 0..<packages.size»
  	namespace «packages.get(i)» {
  	«ENDFOR»
  	'''
  }
  
  def static String printNamespaceEnd(ComponentTypeSymbol comp) {
  	var packages = ComponentHelper.getPackages(comp);
  	return '''
	«FOR i : 0..<packages.size»
	} // namespace «packages.get(packages.size - (i+1))»
	«ENDFOR»
	} // namespace montithings
  	'''
  }

  def static printGetPort(ASTPortAccess access) {
		return '''
		«IF access.isPresentComponent»
			«access.component».
		«ELSE»
			this->
		«ENDIF»
		getPort«access.port.toFirstUpper» ()
		'''.toString().replace("\n", "")
	}

	def static String printExpression(ASTExpression expr, boolean isAssignment) {
    	return CppPrettyPrinter.print(expr);
  	}

	def static String printExpression(ASTExpression expr) {
    	return printExpression(expr, true);
	}

}
