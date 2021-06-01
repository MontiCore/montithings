<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}LogTraceObserver::onEvent ()
{
    if(!comp->shouldCompute()) {
        return;
    }

    bool isInputPresent = false;
    bool isOutputPresent = false;

    ${className}Interface *interface = comp->getInterface();
    ${tc.includeArgs("template.logtracing.helper.ComputeInputs", [comp, config, false, "false"])}
    std::multimap${"<"}sole::uuid, std::string${">"} traceUUIDs;

    ${tc.includeArgs("template.logtracing.helper.FillTraceUuids", [comp, config])}
    ${tc.includeArgs("template.logtracing.helper.CheckOutputs", [comp, config])}

    if (isInputPresent) {
        comp->getLogTracer()->handleInput(${Identifier.getInputName()}, traceUUIDs);
    }

    if (isOutputPresent) {
        comp->getLogTracer()->handleOutput();
    }
}