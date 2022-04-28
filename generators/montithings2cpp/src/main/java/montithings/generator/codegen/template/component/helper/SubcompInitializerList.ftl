<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">


<#list comp.subComponents as subcomponent>
  ${subcomponent.getName()}( instanceName + ".${subcomponent.getName()}"
  <#if brokerIsMQTT && (ComponentHelper.getDynamicallyConnectedSubcomps(comp)?seq_contains(subcomponent.getType()) || splittingModeDisabled)>
    , passedMqttClientInstance
    , passedMqttClientLocalInstance
  </#if>
  <#if splittingModeDisabled || ComponentHelper.shouldIncludeSubcomponents(comp, config)>
    <#list ComponentHelper.getParamValues(subcomponent) as param >
      <#if param?index==0>,</#if>
      ${param}<#sep>,</#sep>
    </#list>
  </#if>)<#sep>,</#sep>
</#list>