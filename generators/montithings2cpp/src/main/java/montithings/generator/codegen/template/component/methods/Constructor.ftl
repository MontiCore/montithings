<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#assign shouldPrintSubcomponents = comp.subComponents?has_content && (config.getSplittingMode().toString() == "OFF")>

${Utils.printTemplateArguments(comp)}
${className}${Utils.printFormalTypeParameters(comp)}::${className}
(std::string instanceName
<#if comp.getParameters()?has_content>
  , ${Utils.printConfigurationParametersAsList(comp)}
</#if>
)
<#if comp.isAtomic() || shouldPrintSubcomponents || comp.getParameters()?has_content>
  :
</#if>
<#if comp.getParameters()?has_content>
  ${Identifier.getStateName()}(
  <#list comp.getParameters() as param >
    ${param.getName()} <#sep>,</#sep>
  </#list>)
  <#if comp.isAtomic() || shouldPrintSubcomponents>,</#if>
</#if>
<#if comp.isAtomic()>
  ${tc.includeArgs("template.component.helper.BehaviorInitializerListEntry", [comp, compname])}
</#if>
<#if shouldPrintSubcomponents>
  ${tc.includeArgs("template.component.helper.SubcompInitializerList", [comp, config])}
</#if>
{
this->instanceName = instanceName;
<#list comp.getParameters() as param >
  ${Identifier.getStateName()}.set${param.getName()?cap_first} (${param.getName()});
</#list>
<#if comp.isAtomic()>
  this->${Identifier.getStateName()}.setInstanceName (instanceName);
  this->${Identifier.getStateName()}.setup ();
  ${tc.includeArgs("template.prepostconditions.hooks.Constructor", [comp])}
</#if>
<#if comp.isPresentParentComponent()>
  super(<#list getInheritedParams(comp) as inhParam >
  inhParam<#sep>,</#sep>
</#list>);
</#if>
}