<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled>
<#if !hasNoPorts>
  ${comp.name}LogTraceObserver* logTraceObserver;
</#if>

<#if brokerIsDDS>
     LogTracerDDSClient*
<#elseif brokerIsMQTT>
     LogTracerMQTTClient*
</#if>
 logTracerInterface;
LogTracer* logTracer;
</#if>
