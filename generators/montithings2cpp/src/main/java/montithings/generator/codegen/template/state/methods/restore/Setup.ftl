<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setup ()
{
<#if config.getMessageBroker().toString() == "MQTT">
  std::string instanceNameTopic = replaceDotsBySlashes (this->instanceName);
  MqttClient::instance ()->addUser (this);
  MqttClient::instance ()->subscribe ("/state/" + instanceNameTopic);
  MqttClient::instance ()->subscribe ("/replayFinished/" + instanceNameTopic);
</#if>
<#list ComponentHelper.getArcFieldVariables(comp) as var>
    <#assign varName = var.getName()>
    <#assign type = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>
    <#if ComponentHelper.hasAgoQualification(comp, var)>
      dequeOf__${varName?cap_first}.push_back(std::make_pair(std::chrono::system_clock::now(), ${Utils.getInitialValue(var)}));
    </#if>
</#list>
}