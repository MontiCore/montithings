<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendKeepAlive(std::string sensorActuatorConfigTopic, std::string portName, std::future<void> keepAliveFuture){
  LOG(DEBUG) << "Start sending keepalive to " << sensorActuatorConfigTopic;
  json j;
  j["occupiedBy"] = this->getInstanceName() + "." + portName;
  std::string message = j.dump();
  mqttClientLocalInstance->unsubscribe (sensorActuatorConfigTopic);
  while (keepAliveFuture.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout){
    mqttClientLocalInstance->publishRetainedMessage (sensorActuatorConfigTopic, message);
    std::this_thread::sleep_for(std::chrono::seconds(5));
  }
  LOG(DEBUG) << "Stop sending keepalive to " << sensorActuatorConfigTopic;
}