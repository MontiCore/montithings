# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._ast.ASTPortAccess
import de.monticore.expressions.expressionsbasis._ast.ASTExpression
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol
import java.util.ArrayList
import java.util.List
import montithings.generator.helper.ComponentHelper
import montithings.generator.helper.CppPrettyPrinter-->

class Utils {

  <#--

  Prints the component's configuration parameters as a comma separated list.
 -->
  def static printConfigurationParametersAsList(ComponentTypeSymbol comp) {
    return '''
      <#list comp.parameters as param >
 param.type.print} ${param.name<#sep>,
 </#list>
    '''.toString().replace("\n", "")
  }

  <#--

  Prints the component's imports
 -->
  def static printImports(ComponentTypeSymbol comp) {
    return '''
      <#list ComponentHelper.getImports(comp) as _import >
 import ${_import.statement}<#if _import.isStar>
 .*
 </#if>;
 </#list>
      <#list comp.innerComponents as inner >
 import ${printPackageWithoutKeyWordAndSemicolon(inner) + "." + inner.name};
 </#list>
    '''
  }

  <#--

  Prints a member of given visibility name and type
 -->
  def static printMember(String type, String name) {
    return '''
      ${type} ${name};
    '''
  }
  
  <#--

  Prints members for configuration parameters.
 -->
  def static printConfigParameters(ComponentTypeSymbol comp) {
    return '''
      <#list comp.parameters as param >
 printMember(ComponentHelper.printCPPTypeName(param.type), param.name)
 </#list>
    '''.toString().replace("\n", "")
  }

  <#--

  Prints members for variables
 -->
  def static printVariables(ComponentTypeSymbol comp) {
    return '''
      <#list ComponentHelper.getFields(comp) as variable >
 printMember(ComponentHelper.printCPPTypeName(variable.type), variable.name)
 </#list>
    '''
  }

  <#--

  Prints formal parameters of a component.
 -->
  def static printFormalTypeParameters(ComponentTypeSymbol comp) {
    return printFormalTypeParameters(comp, false)
  }
  def static printFormalTypeParameters(ComponentTypeSymbol comp, Boolean withClassPrefix) {
    return '''
      <#if comp.hasTypeParameter>
        <
          <#list getGenericParameters(comp) as generic >
 IF withClassPrefix}class </#if>${generic<#sep>,
 </#list>
        >
      </#if>
    '''.toString().replace("\n", "")
  }

  def static String printTemplateArguments(ComponentTypeSymbol comp) {
    return '''
    <#if comp.hasTypeParameter>
 template${Utils.printFormalTypeParameters(comp, true)}
 </#if>
    '''.toString().replace("\n", "")
  }

  def private static List<String> getGenericParameters(ComponentTypeSymbol comp) {
    <#assign List<String> output = new ArrayList>
    if (comp.hasTypeParameter()) {
      <#assign List<TypeVarSymbol> parameterList = comp.getTypeParameters()>
      for (TypeVarSymbol typeParameter : parameterList) {
        output.add(typeParameter.getName())
      }
    }
    return output;
  }
  
  <#--

  Print the package declaration for generated component classes.
  Uses recursive determination of the package name to accomodate for components
  with at least two levels of inner component. These require changing the package name
  to avoid name clashes between the generated packages and the outermost component.
 -->
  def static String printPackage(ComponentTypeSymbol comp) {
    return '''
    <#if comp.isInnerComponent>
 package ${printPackageWithoutKeyWordAndSemicolon(comp.outerComponent.get) + "." + comp.outerComponent.get.name + "gen"};
 <#else>
 package ${comp.packageName};
  </#if>
    '''
  }
  
  <#--

  Helper function used to determine package names.
 -->
  def static String printPackageWithoutKeyWordAndSemicolon(arcbasis._symboltable.ComponentTypeSymbol comp){
    return '''
    <#if comp.isInnerComponent>
 ${printPackageWithoutKeyWordAndSemicolon(comp.outerComponent.get) + "." + comp.outerComponent.get.name + "gen"}
 <#else>
 ${comp.packageName}
  </#if>
    '''
  }
  
  def static String printSuperClassFQ(ComponentTypeSymbol comp){
    <#assign String packageName = printPackageWithoutKeyWordAndSemicolon(comp.parentInfo);>
    if(packageName.equals("")){
      return '''${comp.parent.name}'''
    } else {
      return '''${packageName}.${comp.parent.name}'''
    }
  }
  
  def static String printNamespaceStart(ComponentTypeSymbol comp) {
    <#assign packages = ComponentHelper.getPackages(comp);>
    return '''
    namespace montithings {
    <#list 0..<packages.size as i >
 namespace ${packages.get(i)} {
 </#list>
    '''
  }
  
  def static String printNamespaceEnd(ComponentTypeSymbol comp) {
    <#assign packages = ComponentHelper.getPackages(comp);>
    return '''
  <#list 0..<packages.size as i >
 } // namespace ${packages.get(packages.size - (i+1))}
 </#list>
  } // namespace montithings
    '''
  }

  def static printGetPort(ASTPortAccess access) {
    return '''
    <#if access.isPresentComponent>
 ${access.component}.
 <#else>
 this->
  </#if>
    getPort${access.port.toFirstUpper} ()
    '''.toString().replace("\n", "")
  }

  def static String printExpression(ASTExpression expr, boolean isAssignment) {
      return CppPrettyPrinter.print(expr);
    }

  def static String printExpression(ASTExpression expr) {
      return printExpression(expr, true);
  }

}
