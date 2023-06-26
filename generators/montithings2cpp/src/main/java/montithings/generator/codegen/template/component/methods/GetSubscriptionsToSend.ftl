<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className", "portName")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
std::set< std::string> *
${className}${Utils.printFormalTypeParameters(comp)}::getSubscriptionsToSend${portName} ()
{
return & subscriptionsToSend${portName};
}