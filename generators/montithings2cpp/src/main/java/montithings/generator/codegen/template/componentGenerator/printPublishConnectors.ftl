<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::publishConnectors ()
{
<#list comp.getAstNode().getConnectors() as connector>
  <#list connector.getTargetList() as target>
    // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
    MqttClient::instance()->publish(replaceDotsBySlashes("/connectors/" + instanceName + "/${target.getQName()}"), replaceDotsBySlashes(instanceName + "/${connector.getSource().getQName()}"));
    LOG(DEBUG) << "Published connector via MQTT. "
    << replaceDotsBySlashes("/connectors/" + instanceName + "/${target.getQName()}")
    << " "
    << replaceDotsBySlashes(instanceName + "/${connector.getSource().getQName()}");
  </#list>
</#list>
}