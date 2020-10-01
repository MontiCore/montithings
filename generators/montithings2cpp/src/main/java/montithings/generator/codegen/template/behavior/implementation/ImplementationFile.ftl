# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.behavior

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.helper.ComponentHelper-->
  
  def static String generateImplementationFile(ComponentTypeSymbol comp, String compname, boolean existsHWC) {
    return '''
  #include "${compname}Impl<#if existsHWC>
 TOP
 </#if>.h"
  ${Utils.printNamespaceStart(comp)}
  <#if !comp.hasTypeParameter>
 ${generateImplementationBody(comp, compname, existsHWC)}
 </#if>
  ${Utils.printNamespaceEnd(comp)}
  '''
  }
  
    def static generateImplementationBody(ComponentTypeSymbol comp, String compname, boolean isTOP) {
    <#assign String generics = Utils.printFormalTypeParameters(comp);>
    return '''
<#if ComponentHelper.hasBehavior(comp)>
${Utils.printTemplateArguments(comp)}
${compname}Result${generics} ${compname}Impl<#if isTOP>
 TOP
 </#if>${generics}::getInitialValues(){
  return {};
}

${Utils.printTemplateArguments(comp)}
${compname}Result${generics} ${compname}Impl<#if isTOP>
 TOP
 </#if>${generics}::compute(${compname}Input${generics} ${Identifier.inputName}){
  ${compname}Result${generics} result;
  ${ComponentHelper.printStatementBehavior(comp)}
  return result;
}
</#if>
'''
  }
  
    def static String printConstructor(ComponentTypeSymbol comp, boolean isTOP) {
    return '''
${comp.name}Impl<#if isTOP>
 TOP
 </#if>(${Utils.printConfigurationParametersAsList(comp)})
<#if !comp.parameters.isEmpty>
:
<#list comp.parameters as param >
 ${param.name} (${param.name})<#sep>,
 </#list>
{
}
${ELSE}
= default;
</#if>
'''

  }
