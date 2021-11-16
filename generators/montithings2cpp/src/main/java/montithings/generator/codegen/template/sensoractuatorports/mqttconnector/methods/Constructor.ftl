<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port")}

${port}MqttConnector::${port}MqttConnector(std::string instanceName, MqttClient* passedMqttClientInstance)
{
mqttClientInstance = passedMqttClientInstance;
mqttClientInstance->addUser (this);
this->instanceName = instanceName;
this->uuid = UniqueElement();
}