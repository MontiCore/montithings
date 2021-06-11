<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}LogTraceObserver::onEvent ()
{
    std::vector<sole::uuid> subCompOutputForwards;
    bool isOutputPresent = false;

    if(comp->shouldCompute()) {
        bool isInputPresent = false;
        ${className}Interface *interface = comp->getInterface();
        ${tc.includeArgs("template.logtracing.helper.ComputeInputs", [comp, config, false, "false"])}
        std::multimap${"<"}sole::uuid, std::string${">"} traceUUIDs;

        ${tc.includeArgs("template.logtracing.helper.FillTraceUuids", [comp, config])}

        if (isInputPresent) {
            comp->getLogTracer()->handleInput(${Identifier.getInputName()}, traceUUIDs);
        }
    }


    ${tc.includeArgs("template.logtracing.helper.CheckOutputs", [comp, config])}
    if (isOutputPresent) {
        comp->getLogTracer()->handleOutput(subCompOutputForwards);
    }
}