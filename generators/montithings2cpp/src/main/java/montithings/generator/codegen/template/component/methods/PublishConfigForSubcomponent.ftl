<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


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
    ${Utils.printSIParameters(comp, subcomponent)}
    <#assign typeArgs = TypesHelper.getTypeArguments(subcomponent)>
    <#if typeArgs != "">
    std::string typeArgs = "${typeArgs}";
    config["_typeArgs"] = dataToJson (typeArgs);
    </#if>
    std::string configJson = config.dump ();

    MqttClient::instance ()->publish ("/config/${subcomponentWithSlashes}", configJson);
    CLOG (DEBUG, "MQTT") << "Published config via MQTT. "
                         << "/config/${subcomponentWithSlashes}"
                         << " " << configJson;
  }
</#list>
}