<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}LogTraceObserver::checkOutput ()
{
    bool isOutputPresent = false;

    ${tc.includeArgs("template.logtracing.helper.CheckOutputs", [comp, config])}

    if (isOutputPresent) {
        comp->getLogTracer()->handleOutput();
    }
}