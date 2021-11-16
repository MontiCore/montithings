<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port")}

void ${port}MqttConnector::start(){
threads.push_back(std::thread{&${port}MqttConnector::run, this});
}