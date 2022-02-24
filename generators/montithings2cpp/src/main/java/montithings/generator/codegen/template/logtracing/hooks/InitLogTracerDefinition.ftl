<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
${Utils.printTemplateArguments(comp)}
void ${hostclassName}${Utils.printFormalTypeParameters(comp)}::initLogTracer(
  <#if config.getMessageBroker().toString() == "DDS">
    DDSClient &ddsClient
  </#if>
){

  <#if !(comp.getPorts()?size == 0)> <#-- todo many usages -->
    logTraceObserver = new ${comp.name}LogTraceObserver(this);
  </#if>

  <#if config.getMessageBroker().toString() == "DDS">
    logTracerInterface = new LogTracerDDSClient(ddsClient,
      instanceName, false, true, true, false);
    <#elseif config.getMessageBroker().toString() == "MQTT">
      logTracerInterface = new LogTracerMQTTClient(instanceName, false);
  </#if>

  logTracer = new LogTracer(instanceName, *logTracerInterface);
}
</#if>
