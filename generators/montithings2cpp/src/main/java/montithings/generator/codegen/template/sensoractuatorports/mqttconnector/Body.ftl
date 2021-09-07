<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "isSensor")}

#include "${port}MqttConnector.h"

${tc.includeArgs("template.sensoractuatorports.mqttconnector.methods.Constructor", [port])}
${tc.includeArgs("template.sensoractuatorports.mqttconnector.methods.SetUp", [port, isSensor])}
${tc.includeArgs("template.sensoractuatorports.mqttconnector.methods.Start", [port])}
${tc.includeArgs("template.sensoractuatorports.mqttconnector.methods.Run", [port, isSensor])}
${tc.includeArgs("template.sensoractuatorports.mqttconnector.methods.OnMessage", [port])}
${tc.includeArgs("template.sensoractuatorports.mqttconnector.methods.ThreadJoin", [port])}
