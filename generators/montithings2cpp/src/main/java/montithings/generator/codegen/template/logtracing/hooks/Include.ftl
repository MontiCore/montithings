<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">
<#if logTracingEnabled>
  #include "logtracing/LogTracer.h"

  <#if !hasNoPorts>
    #include "${compname}LogTraceObserver.h"
  </#if>

  <#if brokerIsDDS>
    #include "logtracing/interface/dds/LogTracerDDSClient.h"
  <#elseif brokerIsMQTT>
    #include "logtracing/interface/mqtt/LogTracerMQTTClient.h"
  </#if>
</#if>