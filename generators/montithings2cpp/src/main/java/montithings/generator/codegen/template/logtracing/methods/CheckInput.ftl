<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}LogTraceObserver::checkInput (${compname}Input${Utils.printFormalTypeParameters(comp)} ${Identifier.getInputName()})
{
    bool isInputPresent = false;

    std::multimap${"<"}sole::uuid, std::string${">"} traceUUIDs;

    ${tc.includeArgs("template.logtracing.helper.FillTraceUuids", [comp, config])}

    if (isInputPresent) {
        comp->getLogTracer()->handleInput(${Identifier.getInputName()}, traceUUIDs);
    }

}