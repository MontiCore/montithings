<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::compute(){
// ensure there are no parallel compute() executions
std::lock_guard${"<std::mutex>"} guard(computeMutex);

if (shouldCompute()) {

${tc.includeArgs("template.componentGenerator.printComputeInputs", [comp, compname, false])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}

<#if config.getSplittingMode().toString() == "OFF">
    <#list comp.subComponents as subcomponent >
        this->${subcomponent.getName()}.compute();
    </#list>
</#if>

${tc.includeArgs("template.componentGenerator.printComputeResults", [comp, compname, true, className])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
}
}