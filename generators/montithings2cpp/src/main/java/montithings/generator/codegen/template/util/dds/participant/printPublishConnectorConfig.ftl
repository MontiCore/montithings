<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

json config = json::array({
    <#list comp.getAstNode().getConnectors() as connector>
      <#list connector.getTargetList() as target>
        // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
        comp->getInstanceName() + ".${connector.getSource().getQName()}/out->" + comp->getInstanceName() + ".${target.getQName()}/in",
      </#list>
    </#list>
});

if (!config.empty())
{
    std::string configJson = config.dump();
    connectorPortOut->sendToExternal(configJson);

    CLOG(DEBUG, "DDS")  << "Published connector config: "
                        << configJson;
} else {
    CLOG(DEBUG, "DDS") << "No connector config to publish. ";
}