<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#assign shouldPrintSubcomponents = comp.subComponents?has_content && (splittingModeDisabled || ComponentHelper.shouldIncludeSubcomponents(comp, config))>

${Utils.printTemplateArguments(comp)}
${className}${Utils.printFormalTypeParameters(comp)}::${className}
(std::string instanceName
<#if brokerIsMQTT>
  , MqttClient* passedMqttClientInstance
  , MqttClient* passedMqttClientLocalInstance
</#if>
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
  </#list>),
  state__at__pre(
    <#list comp.getParameters() as param >
      ${param.getName()} <#sep>,</#sep>
    </#list>)
  <#if comp.isAtomic() || shouldPrintSubcomponents>,</#if>
</#if>
<#if comp.isAtomic() || ComponentHelper.getPortSpecificBehaviors(comp)?size gt 0>
  ${tc.includeArgs("template.component.helper.BehaviorInitializerListEntry", [comp, config])}
  <#if !comp.isAtomic()>,</#if>
</#if>
<#if shouldPrintSubcomponents>
  ${tc.includeArgs("template.component.helper.SubcompInitializerList", [comp, config])}
</#if>
{
this->instanceName = instanceName;

<#if brokerIsMQTT>
mqttClientInstance = passedMqttClientInstance;
mqttClientLocalInstance = passedMqttClientLocalInstance;
  <#if true>
    mqttClientCompatibilityInstance = MqttClient::localInstance("192.168.0.10", 1883);
  </#if>
</#if>

<#list comp.getParameters() as param >
  ${Identifier.getStateName()}.set${param.getName()?cap_first} (${param.getName()});
</#list>
<#if comp.isAtomic()>
  this->${Identifier.getStateName()}.setInstanceName (instanceName);
  this->${Identifier.getStateName()}.setup (<#if brokerIsMQTT>mqttClientInstance</#if>);
  ${tc.includeArgs("template.prepostconditions.hooks.Constructor", [comp])}
  state__at__pre = ${Identifier.getStateName()};
</#if>

<#if comp.isPresentParentComponent()>
  super(<#list getInheritedParams(comp) as inhParam >
  inhParam<#sep>,</#sep>
</#list>);
</#if>
}