<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "isSensor", "config")}

#include<iostream>
#include "${port}.h"
#include "tclap/CmdLine.h"
#include "MqttClient.h"

INITIALIZE_EASYLOGGINGPP


int main(int argc, char* argv[]) {

  el::Loggers::getLogger("MQTT_PORT");
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

  MqttClient* mqttClientInstance = MqttClient::localInstance(brokerHostnameArg.getValue (), brokerPortArg.getValue ());

  // Wait for initial connection
  while(!mqttClientInstance->isConnected());

  LOG(DEBUG) << "MQTT Connection Setup.";


  ${port} port (
  instanceNameArg.getValue (),
  mqttClientInstance
  );

  port.setUp();
  <#if isSensor>
    port.start();
  </#if>

  LOG(DEBUG) << "SensorActuator Port ${port} started.";

  port.threadJoin();
  mqttClientInstance->wait();

  }
  catch (TCLAP::ArgException &e) // catch exceptions
  {
  LOG(FATAL) << "error: " << e.error () << " for arg " << e.argId ();
  }
  return 0;
}
