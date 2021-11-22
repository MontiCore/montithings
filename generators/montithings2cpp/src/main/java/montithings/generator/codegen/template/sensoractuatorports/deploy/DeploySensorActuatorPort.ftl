<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "isSensor", "config")}

#include<iostream>
#include "${port}MqttConnector.h"
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
  ${tc.includeArgs("template.sensoractuatorports.deploy.helper.CmdParameters", [port, config])}
  ${tc.includeArgs("template.sensoractuatorports.deploy.helper.ComponentStart", [port, isSensor])}
  
  }
  catch (TCLAP::ArgException &e) // catch exceptions
  {
  LOG(FATAL) << "error: " << e.error () << " for arg " << e.argId ();
  }
  return 0;
}
