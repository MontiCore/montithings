<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign className = comp.getName() + "Manager">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#include "${comp.getName()}Manager.h"
#include "messages/PortToSocket.h"
<#if config.getSplittingMode().toString() == "LOCAL">
  #include "json/json.hpp"
  #include ${"<fstream>"}
</#if>

${Utils.printNamespaceStart(comp)}

<#if config.getSplittingMode().toString() == "LOCAL">
  using json = nlohmann::json;
</#if>

${className}::${className}
(${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp,
std::string managementPort, std::string communicationPort)
: comp (comp), managementPort (managementPort), communicationPort (communicationPort)
{
comm = new ManagementCommunication ();
comm->init(managementPort);
comm->registerMessageProcessor (this);
portConfigFilePath = "ports/" + comp->getInstanceName () + ".json";
}

void
${className}::process (std::string msg)
{
  ${tc.includeArgs("template.util.comm.checkForManagementInstructions", [comp, config])}
}

void
${className}::initializePorts ()
{
  ${tc.includeArgs("template.util.comm.initializePorts", [comp, config])}
}

void
${className}::searchSubcomponents ()
{
  ${tc.includeArgs("template.util.comm.searchForSubComps", [comp, config])}
}

${Utils.printNamespaceEnd(comp)}