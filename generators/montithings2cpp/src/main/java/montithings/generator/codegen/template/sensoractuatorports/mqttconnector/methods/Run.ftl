<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "isSensor")}

void ${port}MqttConnector::run(){
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