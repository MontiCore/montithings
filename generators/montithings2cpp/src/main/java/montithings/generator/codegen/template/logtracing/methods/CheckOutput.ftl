<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/logtracing/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}::checkOutput ()
{
    bool isOutputPresent = false;

    ${tc.includeArgs("template.logtracing.helper.CheckOutputs", [comp, config])}

    if (isOutputPresent) {
        comp->getLogTracer()->handleOutput();
    }
}