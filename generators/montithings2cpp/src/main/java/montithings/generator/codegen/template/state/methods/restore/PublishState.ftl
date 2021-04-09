<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::publishState (json state)
{
MqttClient::instance ()->publish ("/setState/" + replaceDotsBySlashes (this->instanceName), state.dump ());
}