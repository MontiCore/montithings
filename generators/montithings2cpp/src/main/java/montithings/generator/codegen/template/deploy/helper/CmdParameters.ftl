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

<#if comp.getPorts()?size gt 0>
  TCLAP::SwitchArg ppprintConnectionStr ("", "pretty", "Set whether connect string shall be pretty printed (default: false). PrettyPrinted JSONs will include intends and newlines. Not prettyprinted connect strings have escaped quotation marks",
  false);
  cmd.add (ppprintConnectionStr);

  TCLAP::SwitchArg printConnectionStr ("", "printConnectStr", "Prints out the JSON connection string of this component, i.e. a message containing the component's interface that can be passed to other components to enable them to connect to this component, before starting the component",
  false);
  cmd.add (printConnectionStr);

  <#list ComponentHelper.getInterfaceClassNames(comp) as interface>
    TCLAP::SwitchArg printConnectionStr${interface} ("", "printConnectStr${interface}", "Prints out the JSON connection string of this component interpreted as a '${interface}', i.e. a message containing the component's interface that can be passed to other components to enable them to connect to this component, before starting the component",
    false);
    cmd.add (printConnectionStr${interface});
  </#list>
</#if>

<#if config.getSplittingMode().toString() == "LOCAL" && config.getMessageBroker().toString() == "OFF">
  ${tc.includeArgs("template.deploy.helper.CommunicationManagerArgs", [comp, config])}
<#elseif config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.deploy.helper.MqttArgs")}
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