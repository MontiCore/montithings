${tc.signature("port", "isSensor", "config")}

#include "${port}Port.h"
#include "easyloggingpp/easylogging++.h"
#include "IComponent.h"
#include "MqttClient.h"
#include "MqttPort.h"
#include "tl/optional.hpp"
#include <string>
#include <thread>
#include <vector>
#include <iostream>

class ${port} : public MqttUser
{



protected:
std::vector<std::thread> threads;
MqttClient *  mqttClientInstance;
std::string sensorActuatorTopic = ${defineHookPoint("<CppBlock>?portTemplate:topic")};
${port}Port<${defineHookPoint("<CppBlock>?portTemplate:type")}> * sensorActuatorPort;
std::string instanceName;

public:
${port}(std::string instanceName);
void setUp();
void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
void start();
void run();
void threadJoin();
};

${port}::${port}(std::string instanceName)
{
mqttClientInstance = MqttClient::instance ();
mqttClientInstance->addUser (this);
this->instanceName = instanceName;
}



void ${port}::setUp(){

sensorActuatorPort = new ${port}Port<${defineHookPoint("<CppBlock>?portTemplate:type")}>(instanceName);

<#if !isSensor>
mqttClientInstance->subscribe ("/sensorActuator/" + sensorActuatorTopic);
</#if>
}


void ${port}::start(){
    threads.push_back(std::thread{&${port}::run, this});
}

void ${port}::run(){
    <#if isSensor>
    LOG(DEBUG) << "Thread for ${port} started";
    while (true)
    {
        auto end = std::chrono::high_resolution_clock::now()
        + std::chrono::milliseconds(50);
        if(sensorActuatorPort->hasValue(this->uuid)){
            mqttClientInstance.publish("/sensorActuator/" + sensorActuatorTopic, sensorActuatorPort->getCurrentValue(this->uuid));
        }
        do {
            std::this_thread::yield();
            std::this_thread::sleep_for(std::chrono::milliseconds(1));
        } while (std::chrono::high_resolution_clock::now()  < end);
    }
    </#if>
}

void ${port}::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{
    std::string payload = std::string ((char *)message->payload, message->payloadlen);
    sensorActuatorPort->setNextValue(payload);
}

void ${port}::threadJoin (){
    for (int i = 0; i < threads.size (); i++){
        threads[i].join ();
    }
}
