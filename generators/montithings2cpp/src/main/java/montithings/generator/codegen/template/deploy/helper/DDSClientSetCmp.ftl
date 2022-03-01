<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if brokerIsDDS>
    cmp.setDDSCmdArgs(ddsArgc, ddsArgv);
</#if>

${tc.includeArgs("template.logtracing.hooks.InitLogTracer", [comp, config])}

<#if !(splittingModeDisabled) && brokerIsDDS>
  ddsClient.setComp(&cmp);

  ddsClient.initializeOutgoingPorts();
  ddsClient.initializeConnectorConfigPortSub();
  ddsClient.publishConnectorConfig();
</#if>