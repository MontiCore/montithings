<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port")}

#include "${port}Port.h"
#include "easyloggingpp/easylogging++.h"
#include "IComponent.h"
#include "MqttClient.h"
#include "MqttPort.h"
#include "Utils.h"
#include "Message.h"
#include "tl/optional.hpp"
#include <string>
#include <thread>
#include <vector>
#include <iostream>


class ${port}MqttConnector : public MqttUser
{

protected:
std::vector<std::thread> threads;
MqttClient *  mqttClientInstance;
std::string sensorActuatorTopic = ${defineHookPoint("<CppBlock>?portTemplate:topic")};
${port}Port<Message<${defineHookPoint("<CppBlock>?portTemplate:type")}>>* sensorActuatorPort;
std::string instanceName;
UniqueElement uuid;


public:
${port}MqttConnector(std::string instanceName, MqttClient* passedMqttClientInstance);
void setUp();
void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
void start();
void run();
void threadJoin();
};
