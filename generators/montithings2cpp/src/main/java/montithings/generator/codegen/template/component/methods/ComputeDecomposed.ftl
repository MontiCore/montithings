<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::compute(){
// ensure there are no parallel compute() executions
std::lock_guard${"<std::mutex>"} guard(computeMutex);

if (shouldCompute()) {

${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, "false"])}
${compname}State${Utils.printFormalTypeParameters(comp)} ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}

<#if config.getSplittingMode().toString() == "OFF">
    <#list comp.subComponents as subcomponent >
        this->${subcomponent.getName()}.compute();
    </#list>
</#if>

${tc.includeArgs("template.component.helper.ComputeResults", [comp, config, true, className])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
}
}