<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendKeepAlive(std::string sensorActuatorHeartbeatTopic, std::string portName, std::string typeName, std::future<void> keepAliveFuture){
  LOG(DEBUG) << "Start sending keepalive to " << sensorActuatorHeartbeatTopic;
  json j;
  j["occupiedBy"] = this->getInstanceName() + "." + portName;
  j["type"] = typeName;
  std::string message = j.dump();
  while (keepAliveFuture.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout){
    mqttClientLocalInstance->publishRetainedMessage (sensorActuatorHeartbeatTopic, message);
    std::this_thread::sleep_for(std::chrono::seconds(5));
  }
  LOG(DEBUG) << "Stop sending keepalive to " << sensorActuatorHeartbeatTopic;
}