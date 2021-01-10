<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getSplittingMode().toString() == "DISTRIBUTED">
  <#assign argc = 5>
<#else>
  <#assign argc = 3>
</#if>

int ddsArgc = ${argc};
char *ddsArgv[${argc}];
char dds1[] = "-DCPSConfigFile";
char dds3[] = "-DCPSInfoRepo";
ddsArgv[0] = strdup(instanceNameArg.getValue().c_str());
ddsArgv[1] = dds1;
ddsArgv[2] = strdup(dcpsConfigArg.getValue().c_str());
<#if config.getSplittingMode().toString() == "DISTRIBUTED">
  ddsArgv[3] = dds3;
  ddsArgv[4] = strdup(dcpsInfoRepoArg.getValue().c_str());
</#if>

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}DDSParticipant ddsParticipant(&cmp, ddsArgc, ddsArgv);
  ddsParticipant.initializePorts();
  ddsParticipant.publishConnectors();
</#if>