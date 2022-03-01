<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::publishConnectors ()
{
<#list comp.getAstNode().getConnectors() as connector>
  <#list connector.getTargetList() as target>
    // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
    mqttClientInstance->publish(replaceDotsBySlashes("/connectors/" + instanceName + "/${target.getQName()}"), replaceDotsBySlashes(instanceName + "/${connector.getSource().getQName()}"));
    CLOG (DEBUG, "MQTT_PORT") << "Published connector via MQTT. "
    << replaceDotsBySlashes("/connectors/" + instanceName + "/${target.getQName()}")
    << " "
    << replaceDotsBySlashes(instanceName + "/${connector.getSource().getQName()}");
  </#list>
</#list>
}