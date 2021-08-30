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


<#if config.getMessageBroker().toString() == "MQTT">
  std::ifstream file_input("${deploymentConfigPath}");
  sensorActuatorTypes = json::parse(file_input)["sensorActuatorTypes"];

  mqttClientInstance->addUser (this);
  ${tc.includeArgs("template.component.helper.AddMqttOutPorts", [comp, config])}
  ${tc.includeArgs("template.component.helper.AddMqttInPorts", [comp, config])}
</#if>

<#if ComponentHelper.retainState(comp)>
  this->restoreState ();
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  mqttClientInstance->publish (replaceDotsBySlashes ("/components"),
  replaceDotsBySlashes (instanceName));
</#if>

initialize();
}