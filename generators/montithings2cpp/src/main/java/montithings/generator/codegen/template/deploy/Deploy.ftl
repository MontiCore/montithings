<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/deploy/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">


#include "${compname}.h"
<#if !(config.getSplittingMode().toString() == "OFF") && config.getMessageBroker().toString() == "OFF">
  #include "${compname}Manager.h"
<#elseif !(config.getSplittingMode().toString() == "OFF") && config.getMessageBroker().toString() == "DDS">
  #include "${compname}DDSClient.h"
</#if>

#include "tclap/CmdLine.h"
#include "easyloggingpp/easylogging++.h"
#include ${"<chrono>"}
#include ${"<thread>"}
#include ${"<regex>"}
<#if config.getMessageBroker().toString() == "MQTT">
#include "MqttConfigRequester.h"
</#if>

INITIALIZE_EASYLOGGINGPP

int main<#if existsHWC>TOP</#if>(int argc, char* argv[])
{

el::Loggers::getLogger("MQTT_PORT");
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
${tc.includeArgs("template.deploy.helper.CmdParameters", [comp, config])}
${tc.includeArgs("template.deploy.helper.ComponentStart", [comp, config])}
<#if config.getMessageBroker().toString() == "DDS">
  ${tc.includeArgs("template.deploy.helper.DDSCleanup", [comp, config])}
</#if>
}
catch (TCLAP::ArgException &e) // catch exceptions
{
LOG(FATAL) << "error: " << e.error () << " for arg " << e.argId ();
}
return 0;
}