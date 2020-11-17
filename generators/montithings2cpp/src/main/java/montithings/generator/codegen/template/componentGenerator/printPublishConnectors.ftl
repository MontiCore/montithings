<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

void
${comp.name}::publishConnectors ()
{
<#list comp.getAstNode().getConnectors() as connector>
  <#list connector.getTargetList() as target>
    // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
    MqttClient::instance()->publish(replaceDotsBySlashes("/connectors/" + instanceName + "/${target.getQName()}"), replaceDotsBySlashes(instanceName + "/${connector.getSource().getQName()}"));
    std::cout << "Published connector via MQTT. "
    << replaceDotsBySlashes("/connectors/" + instanceName + "/${target.getQName()}")
    << " "
    << replaceDotsBySlashes(instanceName + "/${connector.getSource().getQName()}")
    << std::endl;
  </#list>
</#list>
}