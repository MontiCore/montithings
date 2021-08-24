<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#if config.getSplittingMode().toString() == "DISTRIBUTED">
  <#assign deploymentConfigPath = "~/.montithings/deployment-info.json">
<#else>
  <#assign deploymentConfigPath = "../../deployment-info.json">
</#if>

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}


<#if comp.isPresentParentComponent()>
  super.setUp(enclosingComponentTiming);
</#if>


<#if config.getSplittingMode().toString() == "OFF" || ComponentHelper.shouldIncludeSubcomponents(comp,config)>
  <#list comp.getSubComponents() as subcomponent >
    ${subcomponent.getName()}.setUp(enclosingComponentTiming);
  </#list>

  <#list comp.getAstNode().getConnectors() as connector>
    <#list connector.getTargetList() as target>
      <#if !ComponentHelper.isIncomingPort(comp, target) && config.getMessageBroker().toString() == "OFF">
        // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
        ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.getSource())});
      </#if>
    </#list>
  </#list>
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  mqttClientInstance = MqttClient::instance ();

  std::ifstream file_input("${deploymentConfigPath}");
  json sensorActuatorTypes = json::parse(file_input)["sensorActuatorTypes"];

  mqttClientInstance->addUser (this);
  ${tc.includeArgs("template.component.helper.AddMqttOutPorts", [comp, config])}
  ${tc.includeArgs("template.component.helper.AddMqttInPorts", [comp, config])}
</#if>

<#if ComponentHelper.retainState(comp)>
  this->restoreState ();
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  this->publishConnectors();

  mqttClientInstance->publish (replaceDotsBySlashes ("/components"),
  replaceDotsBySlashes (instanceName));

  mqttClientInstance->subscribe ("/prepareComponent");
  mqttClientInstance->subscribe ("/components");
</#if>

}