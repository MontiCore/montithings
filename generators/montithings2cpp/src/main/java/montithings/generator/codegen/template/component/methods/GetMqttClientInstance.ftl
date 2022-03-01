<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
MqttClient *
${className}${Utils.printFormalTypeParameters(comp)}::getMqttClientInstance () const
{
return mqttClientInstance;
}