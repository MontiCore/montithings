<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
${Utils.printTemplateArguments(comp)}
void ${compname}${Utils.printFormalTypeParameters(comp)}::compute() {
if (shouldCompute())
{
${Identifier.getBehaviorImplName()}.restoreState();
${tc.includeArgs("template.componentGenerator.printComputeInputs", [comp, compname, false])}
${compname}Result${Utils.printFormalTypeParameters(comp)} result;
<#list comp.incomingPorts as port>
<#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
</#list>
${tc.includeArgs("template.componentGenerator.printPreconditionsCheck", [comp, compname])}
result = ${Identifier.getBehaviorImplName()}.compute(input);
<#list comp.getOutgoingPorts() as port>
<#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
</#list>
${tc.includeArgs("template.componentGenerator.printPostconditionsCheck", [comp, compname])}
setResult(result);
${Identifier.getBehaviorImplName()}.storeState();
}
}