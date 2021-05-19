<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

TCLAP::ValueArg${"<"}std::string${">"} instanceNameArg ("n", "name","Fully qualified instance name of the component",true,"","string");
cmd.add ( instanceNameArg );

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

<#if config.getMessageBroker().toString() == "MQTT">
  if (muteMqttLogger.getValue ())
  {
  el::Loggers::reconfigureLogger ("MQTT", el::ConfigurationType::Enabled, "false");
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