<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
<#if !(comp.getPorts()?size == 0)>
  ${comp.name}LogTraceObserver* logTraceObserver;
</#if>

<#if config.getMessageBroker().toString() == "DDS">
     LogTracerDDSClient*
<#elseif config.getMessageBroker().toString() == "MQTT">
     LogTracerMQTTClient*
</#if>
 logTracerInterface;
LogTracer* logTracer;
</#if>
