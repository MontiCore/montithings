<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}


<#if comp.isPresentParentComponent()>
  super.setUp(enclosingComponentTiming);
</#if>


<#if splittingModeDisabled || ComponentHelper.shouldIncludeSubcomponents(comp,config)>
  <#list comp.getSubComponents() as subcomponent >
    ${subcomponent.getName()}.setUp(enclosingComponentTiming);
  </#list>

  <#list comp.getAstNode().getConnectors() as connector>
    <#list connector.getTargetList() as target>
      <#if !ComponentHelper.isIncomingPort(comp, target) && (brokerDisabled || ComponentHelper.shouldIncludeSubcomponents(comp,config))>
        // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
        ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.getSource())});
      </#if>
    </#list>
  </#list>
</#if>

<#if brokerIsMQTT>
  std::ifstream file_input("/.montithings/deployment-config.json");
  if(!file_input.good()){
    file_input.close();
    file_input.open("../../deployment-config.json");
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

${tc.includeArgs("template.component.helper.SetupPorts", [comp, config, className])}

<#if ComponentHelper.retainState(comp)>
  this->restoreState ();
</#if>

<#if brokerIsMQTT>
  this->publishConnectors();

  mqttClientInstance->publish (replaceDotsBySlashes ("/components"),
  replaceDotsBySlashes (instanceName));

  mqttClientInstance->subscribe ("/prepareComponent");
  mqttClientInstance->subscribe ("/components");
</#if>

}