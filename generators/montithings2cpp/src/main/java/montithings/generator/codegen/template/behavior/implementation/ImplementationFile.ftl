# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "compname", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#--package montithings.generator.codegen.xtend.behavior

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.helper.ComponentHelper-->

  #include "${compname}Impl<#if existsHWC>
 TOP
 </#if>.h"
  ${Utils.printNamespaceStart(comp)}
  <#if !comp.hasTypeParameter()>
 <@generateImplementationBody comp compname existsHWC/>
 </#if>
  ${Utils.printNamespaceEnd(comp)}
  
    <#macro generateImplementationBody comp compname isTOP>
    <#assign generics = Utils.printFormalTypeParameters(comp)>
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
</#macro>
  
    <#macro printConstructor comp isTOP>
${comp.getName()}Impl<#if isTOP>
 TOP
 </#if>(${Utils.printConfigurationParametersAsList(comp)})
<#if comp.getParameters()?has_content>
:
<#list comp.getParameters() as param >
 ${param.getName()} (${param.getName()})<#sep>,
 </#list>
{
}
<#else>
= default;
</#if>
    </#macro>
