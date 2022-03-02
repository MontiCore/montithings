<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled>
<#if ComponentHelper.componentHasPorts(comp)>
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
