<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/logtracing/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}::onEvent ()
{
    this->checkOutput();
}