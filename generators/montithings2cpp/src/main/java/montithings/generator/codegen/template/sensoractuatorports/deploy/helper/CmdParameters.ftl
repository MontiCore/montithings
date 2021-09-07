<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config")}

TCLAP::CmdLine cmd("${port} MontiThings SensorActuatorPort", ' ', "${config.getProjectVersion()}");
TCLAP::ValueArg${"<"}std::string${">"} instanceNameArg ("n", "name","Fully qualified instance name of the sensorActuatorPort",true,"","string");
cmd.add ( instanceNameArg );

TCLAP::ValueArg${"<"}std::string${">"} brokerHostnameArg ("","brokerHostname","Hostname (or IP address) of the MQTT broker",false,"localhost","string");
TCLAP::ValueArg${"<"}int${">"} brokerPortArg ("","brokerPort","Network port of the MQTT broker",false,1883,"int");
cmd.add ( brokerHostnameArg );
cmd.add ( brokerPortArg );


TCLAP::SwitchArg muteMqttLogger ("", "muteMQTT", "Suppress all logs from MQTT broker", false);
cmd.add (muteMqttLogger);
<#if config.getRecordingMode().toString() == "ON">
  TCLAP::SwitchArg muteRecorder ("", "muteRecorder", "Suppress all logs from the recorder", false);
  cmd.add (muteRecorder);
</#if>

cmd.parse ( argc, argv );

if (muteMqttLogger.getValue ())
{
el::Loggers::reconfigureLogger ("MQTT", el::ConfigurationType::Enabled, "false");
}

<#if config.getRecordingMode().toString() == "ON">
  if (muteRecorder.getValue ())
  {
  el::Loggers::reconfigureLogger ("RECORDER", el::ConfigurationType::Enabled, "false");
  }
</#if>