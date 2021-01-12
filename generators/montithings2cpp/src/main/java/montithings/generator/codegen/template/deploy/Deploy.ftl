<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${compname}.h"
<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
  #include "${compname}Manager.h"
<#elseif config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
  #include "${compname}DDSParticipant.h"
</#if>
#include "tclap/CmdLine.h"
#include ${"<chrono>"}
#include ${"<thread>"}
<#if config.getMessageBroker().toString() == "MQTT">
#include "MqttConfigRequester.h"
</#if>

int main<#if existsHWC>TOP</#if>(int argc, char* argv[])
{
try
{
TCLAP::CmdLine cmd("${compname} MontiThings component", ' ', "${config.getProjectVersion()}");
${tc.includeArgs("template.deploy.CmdParameters", [comp, config])}
${tc.includeArgs("template.deploy.ComponentStart", [comp, config])}
<#if config.getMessageBroker().toString() == "DDS">
  ${tc.includeArgs("template.deploy.DDSParticipantCleanup", [comp, config])}
</#if>
}
catch (TCLAP::ArgException &e) // catch exceptions
{
std::cerr << "error: " << e.error () << " for arg " << e.argId () << std::endl;
}
return 0;
}