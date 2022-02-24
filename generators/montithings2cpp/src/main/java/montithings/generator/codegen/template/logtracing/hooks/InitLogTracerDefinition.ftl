<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled>
${Utils.printTemplateArguments(comp)}
void ${hostclassName}${Utils.printFormalTypeParameters(comp)}::initLogTracer(
  <#if brokerIsDDS>
    DDSClient &ddsClient
  </#if>
){

  <#if !hasNoPorts>
    logTraceObserver = new ${comp.name}LogTraceObserver(this);
  </#if>

  <#if brokerIsDDS>
    logTracerInterface = new LogTracerDDSClient(ddsClient,
      instanceName, false, true, true, false);
    <#elseif brokerIsMQTT>
      logTracerInterface = new LogTracerMQTTClient(instanceName, false);
  </#if>

  logTracer = new LogTracer(instanceName, *logTracerInterface);
}
</#if>
