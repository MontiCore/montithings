${tc.signature("port", "isSensor", "topic", "type", "config", "existsHWC")}

#include "${port}Port.h"
#include "${port}Interface.h"
#include "easyloggingpp/easylogging++.h"
#include "IComponent.h"
#include "MqttClient.h"
#include "MqttPort.h"
#include "tl/optional.hpp"
#include <string>
#include <thread>
#include <vector>
#include <mutex>
#include <iostream>

class ${port} : public IComponent
    , public MqttUser
{


TimeMode timeMode = EVENTBASED ;


protected:
${port}Interface interface;
std::vector<std::thread> threads;
unsigned int remainingComputes = 0;
std::mutex computeMutex;
void initialize();

public:
${port}(std::string instanceName);
void setUp(TimeMode enclosingComponentTiming) override;
${port}Interface* getInterface();
void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
void init() override;
void compute() override;
void start() override;
void run();
void onEvent() override;
void threadJoin();
void setResult(tl::optional<${type}> result);
bool shouldCompute();
};

${port}::${port}(std::string instanceName)
{
this->instanceName = instanceName;
}

${port}Interface* ${port}::getInterface(){
return &interface;
}


void ${port}::initialize(){

interface.getPortIn ()->attach (this);
<#if isSensor>
    interface.addInPortIn(new ${port}Port<${type}>(instanceName));
<#else>
    interface.addOutPortOut(new ${port}Port<${type}>(instanceName));
</#if>

LOG(DEBUG) << "Initialized Mqtt Ports";
}

void ${port}::setUp(TimeMode enclosingComponentTiming){

<#if isSensor>
// outgoing port out
MqttPort<${type}> *out = new MqttPort<${type}>("${port}/out");
this->interface.addOutPortOut (out);
out->setSensorActuatorName ("${topic}", false);
<#else>
// port in incoming
MqttPort<${type}> *in = new MqttPort<${type}>("${port}/in");
interface.getPortIn ()->attach (this);
this->interface.addInPortIn (in);
in->setSensorActuatorName ("${topic}", true);
</#if>



MqttClient::instance ()->addUser (this);


initialize();
}

void ${port}::init(){
}
void ${port}::compute(){
    // ensure there are no parallel compute() executions
    if (remainingComputes > 0){
        remainingComputes++;
        return;
    }
    std::lock_guard<std::mutex> guard(computeMutex);

    remainingComputes++;
    while (remainingComputes > 0){
        if (shouldCompute()){
            setResult(interface.getPortIn()->getCurrentValue(this->uuid));
        }
        remainingComputes--;
    }
}
void ${port}::start(){
    threads.push_back(std::thread{&${port}::run, this});
}
void ${port}::run(){
    <#if isSensor>
    //TODO: get sensor values in TimeInterval
    </#if>
}
void ${port}::onEvent(){
    this->compute();
}
void ${port}::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{}
void ${port}::threadJoin (){
    for (int i = 0; i < threads.size (); i++){
        threads[i].join ();
    }
}
void ${port}::setResult(tl::optional<${type}> result){
    this->interface.getPortOut()->setNextValue(result);
}
bool ${port}::shouldCompute() {
    if (interface.getPortIn()->hasValue(this->uuid)){
        return true;
    }
    return false;
}