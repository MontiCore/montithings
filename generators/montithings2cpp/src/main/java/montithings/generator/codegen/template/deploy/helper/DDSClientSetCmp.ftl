<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getMessageBroker().toString() == "DDS">
    cmp.setDDSCmdArgs(ddsArgc, ddsArgv);
</#if>

${tc.includeArgs("template.logtracing.hooks.InitLogTracer", [comp, config])}

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS"> <#-- todo invert -->
  ddsClient.setComp(&cmp);

  ddsClient.initializeOutgoingPorts();
  ddsClient.initializeConnectorConfigPortSub();
  ddsClient.publishConnectorConfig();
</#if>