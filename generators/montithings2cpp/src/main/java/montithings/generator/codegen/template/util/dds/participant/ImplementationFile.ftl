<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign className = comp.getName() + "DDSParticipant">


#include "${className}.h"

${Utils.printNamespaceStart(comp)}

${className}::${className}
(std::string instanceName, int argc, char *argv[]) : instanceName(instanceName)
{
  ${tc.includeArgs("template.util.dds.participant.printConstructor", [comp, config])}
}


void ${className}::onNewConnectors(std::string payload)
{
  ${tc.includeArgs("template.util.dds.participant.printOnNewConnectors", [comp, config])}
}

void
${className}::initializeOutgoingPorts ()
{
  ${tc.includeArgs("template.util.dds.participant.printInitializeOutgoingPorts", [comp, config])}
}


void
${className}::publishConnectorConfig ()
{
  ${tc.includeArgs("template.util.dds.participant.printPublishConnectorConfig", [comp, config])}
}

void
${className}::publishParameterConfig ()
{
  ${tc.includeArgs("template.util.dds.participant.printPublishParameterConfig", [comp, config])}
}



${Utils.printNamespaceEnd(comp)}