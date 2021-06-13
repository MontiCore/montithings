<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">
<#if config.getLogTracing().toString() == "ON">
  #include "logtracing/LogTracer.h"

  <#if !(comp.getPorts()?size == 0)>
    #include "${compname}LogTraceObserver.h"
  </#if>

  <#if config.getMessageBroker().toString() == "DDS">
    #include "logtracing/interface/dds/LogTracerDDSClient.h"
  <#elseif config.getMessageBroker().toString() == "MQTT">
    #include "logtracing/interface/mqtt/LogTracerMQTTClient.h"
  </#if>
</#if>