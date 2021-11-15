<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "isSensor")}

void ${port}MqttConnector::setUp(){

sensorActuatorPort = new ${port}Port<Message<${defineHookPoint("<CppBlock>?portTemplate:type")}>>(instanceName);

<#if !isSensor>
    mqttClientInstance->subscribe ("/sensorActuator/" + sensorActuatorTopic);
</#if>
}