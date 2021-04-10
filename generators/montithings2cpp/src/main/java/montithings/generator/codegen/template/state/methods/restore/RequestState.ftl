<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::requestState ()
{
MqttClient::instance ()->publish ("/getState/" + replaceDotsBySlashes (this->instanceName), "");
}