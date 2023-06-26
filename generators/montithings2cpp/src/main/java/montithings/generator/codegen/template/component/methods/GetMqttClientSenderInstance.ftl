<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className","portName")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
MqttClient *
${className}${Utils.printFormalTypeParameters(comp)}::getMqttClientSenderInstance${portName} () const
{
return mqttClientSenderInstance${portName};
}