<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::publishConfigForSubcomponent (std::string instanceName)
{
<#list comp.subComponents as subcomponent >
  <#assign subcomponentWithSlashes = subcomponent.getFullName()?replace(".", "/")>
  if (instanceName == "${subcomponentWithSlashes}")
  {
    json config;
    ${Utils.printSerializeParameters(subcomponent)}
    std::string sourceConfigJson = config.dump ();

    MqttClient::instance ()->publish ("/config/${subcomponentWithSlashes}", sourceConfigJson);
    std::cout << "Published config via MQTT. "
              << "/config/${subcomponentWithSlashes}"
              << " " << sourceConfigJson << std::endl;
  }
</#list>
}