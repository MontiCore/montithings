<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className","portName")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::setIsConnected${portName} ()
{
isConnected${portName} = true;
}