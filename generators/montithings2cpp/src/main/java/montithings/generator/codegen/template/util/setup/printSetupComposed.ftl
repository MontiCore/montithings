<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void ${compname}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
<#if comp.isPresentParentComponent()>
  super.setUp(enclosingComponentTiming);
</#if>


<#if config.getSplittingMode().toString() == "OFF">
  <#list comp.getSubComponents() as subcomponent >
    ${subcomponent.getName()}.setUp(enclosingComponentTiming);
  </#list>

  <#list comp.getAstNode().getConnectors() as connector>
    <#list connector.getTargetList() as target>
      <#if !ComponentHelper.isIncomingPort(comp, target)>
        // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
        ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.getSource())});
      </#if>
    </#list>
  </#list>
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.util.ports.printAddMqttPorts", [comp, config])}
  this->publishConnectors();

  MqttClient::instance ()->addUser (this);
  MqttClient::instance ()->publish (replaceDotsBySlashes ("/components"),
  replaceDotsBySlashes (instanceName));

  MqttClient::instance ()->subscribe ("/components");
</#if>

}