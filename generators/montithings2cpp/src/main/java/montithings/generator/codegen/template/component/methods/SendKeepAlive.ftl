<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendKeepAlive(std::string sensorActuatorTopic, std::string portName, std::future<void> keepAliveFuture){
  LOG(DEBUG) << "Start sending keepalive to " << sensorActuatorTopic;
  json j;
  j["occupiedBy"] = "${comp.getName()?cap_first}" + portName;
  std::string message = j.dump();
  mqttClientInstance->unsubscribe (sensorActuatorTopic);
  while (keepAliveFuture.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout){
    mqttClientInstance->publishRetainedMessage (sensorActuatorTopic, message);
    std::this_thread::sleep_for(std::chrono::seconds(5));
  }
  LOG(DEBUG) << "Stop sending keepalive to " << sensorActuatorTopic;
}