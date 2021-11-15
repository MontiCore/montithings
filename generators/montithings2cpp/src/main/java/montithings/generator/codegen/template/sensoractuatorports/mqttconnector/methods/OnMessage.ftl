<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port")}

void ${port}MqttConnector::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{
std::string payload = std::string ((char *)message->payload, message->payloadlen);
Message<${defineHookPoint("<CppBlock>?portTemplate:type")}> data = jsonToData<Message<${defineHookPoint("<CppBlock>?portTemplate:type")}>>(payload);
sensorActuatorPort->setNextValue(data);
}