<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

TCLAP::ValueArg${"<"}std::string${">"} instanceNameArg ("n", "name","Fully qualified instance name of the component",true,"","string");
cmd.add ( instanceNameArg );

TCLAP::SwitchArg muteTimestamps ("", "muteTimestamps", "Suppress all time stamps in logs",
false);
cmd.add (muteTimestamps);

TCLAP::SwitchArg monochrome ("", "monochrome", "Do not use colored output, use plain text output",
false);
cmd.add (monochrome);

<#if config.getSplittingMode().toString() == "LOCAL" && config.getMessageBroker().toString() == "OFF">
  ${tc.includeArgs("template.deploy.helper.CommunicationManagerArgs", [comp, config])}
<#elseif config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.deploy.helper.MqttArgs", [comp, config])}
<#elseif config.getMessageBroker().toString() == "DDS">
  ${tc.includeArgs("template.deploy.helper.DDSArgs", [comp, config])}
</#if>

<#if config.getRecordingMode().toString() == "ON">
    TCLAP::SwitchArg muteRecorder ("", "muteRecorder", "Suppress all logs from the recorder", false);
    cmd.add (muteRecorder);
</#if>

cmd.parse ( argc, argv );

if (muteTimestamps.getValue ())
{
defaultConf.set (el::Level::Global, el::ConfigurationType::Format, "%level: %msg");
el::Loggers::reconfigureAllLoggers (defaultConf);
}

if (monochrome.getValue ())
{
el::Loggers::removeFlag(el::LoggingFlag::ColoredTerminalOutput);
}



<#if config.getMessageBroker().toString() == "MQTT">
  if (muteMqttLogger.getValue ())
  {
  el::Loggers::reconfigureLogger ("MQTT_PORT", el::ConfigurationType::Enabled, "false");
  }
<#elseif config.getMessageBroker().toString() == "DDS">
  if (muteDdsLogger.getValue ())
  {
  el::Loggers::reconfigureLogger ("DDS", el::ConfigurationType::Enabled, "false");
  }
</#if>

<#if config.getRecordingMode().toString() == "ON">
  if (muteRecorder.getValue ())
  {
    el::Loggers::reconfigureLogger ("RECORDER", el::ConfigurationType::Enabled, "false");
  }
</#if>