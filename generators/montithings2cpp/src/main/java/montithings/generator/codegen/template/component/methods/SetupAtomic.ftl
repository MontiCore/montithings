<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}

<#if comp.isPresentParentComponent()>
  super.setUp(enclosingComponentTiming);
</#if>


<#if brokerIsMQTT>
  std::ifstream file_input("../../deployment-config.json");
  if(!file_input.good()){
    file_input.close();
    file_input.open("/.montithings/deployment-config.json");
    if(!file_input.good()){
      LOG(ERROR) << "No deployment-config file provided.";
    }
  }

  sensorActuatorTypes = json::parse(file_input)["sensorActuatorTypes"];

  mqttClientInstance->addUser (this);
  mqttClientLocalInstance->addUser (this);

  ${tc.includeArgs("template.component.helper.AddMqttOutPorts", [comp, config])}
  ${tc.includeArgs("template.component.helper.AddMqttInPorts", [comp, config])}
</#if>

<#if ComponentHelper.retainState(comp)>
  this->restoreState ();
</#if>

<#if brokerIsMQTT>
  mqttClientInstance->publish (replaceDotsBySlashes ("/components"),
  replaceDotsBySlashes (instanceName));
</#if>

${tc.includeArgs("template.component.helper.SetupPorts", [comp, config, className])}
}