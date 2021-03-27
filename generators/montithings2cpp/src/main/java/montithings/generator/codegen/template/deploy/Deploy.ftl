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
#include "easyloggingpp/easylogging++.h"
#include ${"<chrono>"}
#include ${"<thread>"}
<#if config.getMessageBroker().toString() == "MQTT">
#include "MqttConfigRequester.h"
</#if>

INITIALIZE_EASYLOGGINGPP

int main<#if existsHWC>TOP</#if>(int argc, char* argv[])
{

el::Loggers::getLogger("MQTT");
el::Loggers::getLogger("DDS");
el::Loggers::getLogger("RECORDER");

el::Configurations defaultConf;
defaultConf.setToDefault();
defaultConf.set(el::Level::Global,
el::ConfigurationType::Format,
"%level: %datetime %msg");
defaultConf.set(el::Level::Global,
el::ConfigurationType::ToFile,
"false");
el::Loggers::reconfigureAllLoggers(defaultConf);
el::Loggers::addFlag(el::LoggingFlag::ColoredTerminalOutput);

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
LOG(FATAL) << "error: " << e.error () << " for arg " << e.argId ();
}
return 0;
}