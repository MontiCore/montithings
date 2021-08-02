<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
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

  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}DDSClient ddsClient(instanceNameArg.getValue(), ddsArgc, ddsArgv);
  <#if (comp.getParameters()?size > 0)>
    ddsClient.initializeParameterConfigPortSub();
  </#if>

  ddsClient.publishParameterConfig();

  <#if comp.getParameters()?size = 0>
    // No parameters expected, skip waiting
    ddsClient.setReceivedParameterConfigTrue();
  </#if>

  LOG(DEBUG) << "Waiting for config...";
  while (!ddsClient.isReceivedParameterConfig()){}

  json config = ddsClient.getParameterConfig();

  <#list comp.getParameters() as variable>
    <#assign typeName = TypesPrinter.printCPPTypeName(variable.getType())>
    ${typeName} ${variable.getName()} = jsonToData${"<"}${typeName}${">"}(config["${variable.getName()}"]);
  </#list>
  <#list ComponentHelper.getSIUnitPortNames(comp) as portName>
    double ${portName}ConversionFactor = jsonToData${"<"}double${">"}(config["${portName}ConversionFactor"]);
  </#list>
</#if>
