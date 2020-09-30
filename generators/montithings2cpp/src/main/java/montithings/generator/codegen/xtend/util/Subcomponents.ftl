# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentInstanceSymbol
import arcbasis._symboltable.ComponentTypeSymbol
import java.util.HashSet
import java.util.Set
import montithings.generator.codegen.ConfigParams
import montithings.generator.helper.ComponentHelper-->

class Subcomponents {
  
  def static String printIncludes(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    <#assign Set<String> compIncludes = new HashSet<String>()>
    for (subcomponent : comp.subComponents) {
      <#assign isInner = subcomponent.type.loadedSymbol.isInnerComponent>
      compIncludes.add('''#include "${ComponentHelper.getPackagePath(comp, subcomponent)}<#if isInner}${comp.name>
 -Inner/
 </#if>${ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config, false)}.h"''')
    <#assign Set<String> genericIncludes = ComponentHelper.includeGenericComponent(comp, subcomponent)>
    for (String genericInclude : genericIncludes) {
      compIncludes.add('''#include "${genericInclude}.h"''')
    }
  }
  return '''
  <#list compIncludes as include >
 include
 </#list>
  #include "${compname}Input.h"
  #include "${compname}Result.h"
  '''
  }

  def static String printVars(ComponentTypeSymbol comp, ConfigParams config) {
    return '''
    <#if config.getSplittingMode() == ConfigParams.SplittingMode.OFF>
      <#list comp.subComponents as subcomponent>
        <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
        ${printPackageNamespace(comp, subcomponent)}${type} ${subcomponent.name};
      </#list>
    ${ELSE}
      <#list comp.subComponents as subcomponent >
 std::string subcomp${subcomponent.name.toFirstUpper}IP;
 </#list>
    </#if>
    '''
  }

  def static String printMethodDeclarations(ComponentTypeSymbol comp, ConfigParams config) {
    return '''
    <#list comp.subComponents as subcomponent>
          std::string get${subcomponent.name.toFirstUpper}IP();
          void set${subcomponent.name.toFirstUpper}IP(std::string ${subcomponent.name}IP);
    </#list>
    '''
  }

  def static String printMethodDefinitions(ComponentTypeSymbol comp, ConfigParams config) {
    return '''
    <#list comp.subComponents as subcomponent>
        std::string ${comp.name}::get${subcomponent.name.toFirstUpper}IP(){
          return subcomp${subcomponent.name.toFirstUpper}IP;
        }
        void ${comp.name}::set${subcomponent.name.toFirstUpper}IP(std::string ${subcomponent.name}IP){
          subcomp${subcomponent.name.toFirstUpper}IP = ${subcomponent.name}IP;
        }
    </#list>
    '''
  }

  def static String printInitializerList(ComponentTypeSymbol comp, ConfigParams config) {
    <#assign helper = new ComponentHelper(comp)>
    return '''
      <#list comp.subComponents as subcomponent>
        ${subcomponent.name}( "${subcomponent.name}"
          <#if config.getSplittingMode() == ConfigParams.SplittingMode.OFF>
          <#list helper.getParamValues(subcomponent) BEFORE ',' as param >
 param
 </#list>
          </#if>)<#sep>,
      </#list>
    '''
  }
  
  def static String printPackageNamespace(ComponentTypeSymbol comp, ComponentInstanceSymbol subcomp) {
    <#assign subcomponentType = subcomp.typeInfo>
    <#assign fullNamespaceSubcomponent = ComponentHelper.printPackageNamespaceForComponent(subcomponentType)>
    <#assign fullNamespaceEnclosingComponent = ComponentHelper.printPackageNamespaceForComponent(comp)>
    if (!fullNamespaceSubcomponent.equals(fullNamespaceEnclosingComponent) && 
      fullNamespaceSubcomponent.startsWith(fullNamespaceEnclosingComponent)) {
      return fullNamespaceSubcomponent.split(fullNamespaceEnclosingComponent).get(1)
    } else {
      return fullNamespaceSubcomponent
    }
  }
}
