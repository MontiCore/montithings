<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

json config;

<#list comp.getSubComponents() as subcomponent>
    config[instanceName + ".${subcomponent.getName()}"] = json::object();

    <#assign parameter_index = 0>
    <#list subcomponent.getType().getParameters() as parameter>
        config[instanceName + ".${subcomponent.getName()}"]${Utils.printSerializeParameters(subcomponent)?replace('config','')}
        <#assign parameter_index = parameter_index + 1>
    </#list>
</#list>

if (!config.empty())
{
    initializeParameterConfigPortPub();

    for (auto &paraConfig : config.items()) {
        configPortOut->sendToExternal(config[paraConfig.key()].dump());
    }

    CLOG(DEBUG, "DDS")  << "Published parameter config: " << config.dump();
} else {
    CLOG(DEBUG, "DDS") << "No parameter config to publish. ";
}
