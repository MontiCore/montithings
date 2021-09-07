<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port")}

void ${port}MqttConnector::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{
std::string payload = std::string ((char *)message->payload, message->payloadlen);
sensorActuatorPort->setNextValue(payload);
}