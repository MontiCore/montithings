<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
<#if comp.isPresentParentComponent()>
  super.setUp(enclosingComponentTiming);
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.component.helper.AddMqttInPorts", [comp, config])}
</#if>

<#if ComponentHelper.retainState(comp)>
  this->restoreState ();
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.component.helper.AddMqttOutPorts", [comp, config])}

  MqttClient::instance ()->addUser (this);
  MqttClient::instance ()->publish (replaceDotsBySlashes ("/components"),
  replaceDotsBySlashes (instanceName));
</#if>

initialize();
}