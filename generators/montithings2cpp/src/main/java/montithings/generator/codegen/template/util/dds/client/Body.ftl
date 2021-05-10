<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign className = compname + "DDSClient">

${className}::${className}
(std::string instanceName, int argc, char *argv[]) : instanceName(instanceName)
{
  ${tc.includeArgs("template.util.dds.client.methods.printConstructor", [comp, config])}
}


void ${className}::onNewConnectors(std::string payload)
{
  ${tc.includeArgs("template.util.dds.client.methods.printOnNewConnectors", [comp, config])}
}

void
${className}::initializeOutgoingPorts ()
{
  ${tc.includeArgs("template.util.dds.client.methods.printInitializeOutgoingPorts", [comp, config])}
}

void
${className}::publishConnectorConfig ()
{
  ${tc.includeArgs("template.util.dds.client.methods.printPublishConnectorConfig", [comp, config])}
}

void
${className}::publishParameterConfig ()
{
  ${tc.includeArgs("template.util.dds.client.methods.printPublishParameterConfig", [comp, config])}
}