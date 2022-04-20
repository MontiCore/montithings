<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "isSensor")}

void ${port}MqttConnector::setUp(){

sensorActuatorPort = new ${port}Port<Message<${defineHookPoint("<CppBlock>?portTemplate:type")}>>(instanceName);

<#if !isSensor>
    mqttClientInstance->subscribe ("/sensorActuator/data/" + this->uuid.getUuid().str());
</#if>
    mqttClientInstance->publishRetainedMessage ("/sensorActuator/offer/" + this->uuid.getUuid().str(), "{\"topic\":\"" + this->uuid.getUuid().str() + "\", \"spec\":{\"type\":\"" + sensorActuatorTopic + "\"}}");
}