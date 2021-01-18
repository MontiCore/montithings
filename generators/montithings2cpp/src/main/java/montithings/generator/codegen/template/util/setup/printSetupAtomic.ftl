<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config", "className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
<#if comp.isPresentParentComponent()>
  super.setUp(enclosingComponentTiming);
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.util.ports.printAddMqttInPorts", [comp, config])}
</#if>

<#if ComponentHelper.retainState(comp)>
  this->restoreState ();
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.util.ports.printAddMqttOutPorts", [comp, config])}

  MqttClient::instance ()->addUser (this);
  MqttClient::instance ()->publish (replaceDotsBySlashes ("/components"),
  replaceDotsBySlashes (instanceName));
</#if>

initialize();
}