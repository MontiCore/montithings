<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setup (<#if config.getMessageBroker().toString() == "MQTT">MqttClient* passedMqttClientInstance</#if>)
{
<#if config.getMessageBroker().toString() == "MQTT">
  std::string instanceNameTopic = replaceDotsBySlashes (this->instanceName);
  mqttClientInstance = passedMqttClientInstance;
  mqttClientInstance->addUser (this);
  mqttClientInstance->subscribe ("/state/" + instanceNameTopic);
  mqttClientInstance->subscribe ("/replayFinished/" + instanceNameTopic);
</#if>
<#list ComponentHelper.getArcFieldVariables(comp) as var>
    <#assign varName = var.getName()>
    <#assign type = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>
    <#if ComponentHelper.hasAgoQualification(comp, var)>
      dequeOf__${varName?cap_first}.push_back(std::make_pair(std::chrono::system_clock::now(), ${Utils.getInitialValue(var)}));
    </#if>
</#list>
}