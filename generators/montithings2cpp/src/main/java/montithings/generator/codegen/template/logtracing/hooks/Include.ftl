<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">
<#if config.getLogTracing().toString() == "ON">
  #include "logtracing/LogTracer.h"
</#if>

<#if config.getMessageBroker().toString() == "DDS">
  #include "logtracing/interface/dds/LogTracerDDSClient.h"
</#if>
