<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">

  <#if config.getMessageBroker().toString() == "DDS">
    cmp.initLogTracer (ddsClient);
  <#elseif config.getMessageBroker().toString() == "MQTT">
    cmp.initLogTracer ();
  </#if>

</#if>
