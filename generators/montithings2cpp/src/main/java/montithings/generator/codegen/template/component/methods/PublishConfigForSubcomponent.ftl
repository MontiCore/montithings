<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::publishConfigForSubcomponent (std::string instanceName)
{
std::string thisInstance = replaceDotsBySlashes (this->getInstanceName());
<#list comp.subComponents as subcomponent >
  <#assign subcomponentName = subcomponent.getName()>
  if (instanceName == thisInstance + "/${subcomponentName}")
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

    MqttClient::instance ()->publish ("/config/" + thisInstance + "/${subcomponentName}", configJson);
    CLOG (DEBUG, "MQTT") << "Published config via MQTT. "
                         << "/config/" + thisInstance + "/${subcomponentName}"
                         << " " << configJson;
  }
</#list>
}