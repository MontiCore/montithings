<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "isSensor")}

MqttClient* mqttClientInstance = MqttClient::localInstance(localHostnameArg.getValue (), brokerPortArg.getValue ());

// Wait for initial connection
while(!mqttClientInstance->isConnected());

LOG(DEBUG) << "MQTT Connection Setup.";


${port}MqttConnector port (
instanceNameArg.getValue (),
mqttClientInstance
);

port.setUp();
<#if isSensor>
  port.start();
</#if>

LOG(DEBUG) << "SensorActuator Port ${port} started.";

port.threadJoin();
mqttClientInstance->wait();