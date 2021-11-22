<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port")}

void ${port}MqttConnector::threadJoin (){
for (int i = 0; i < threads.size (); i++){
threads[i].join ();
}
}