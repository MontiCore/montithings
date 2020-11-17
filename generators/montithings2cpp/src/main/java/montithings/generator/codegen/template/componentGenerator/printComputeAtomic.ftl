<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

${Utils.printTemplateArguments(comp)}
void ${compname}${Utils.printFormalTypeParameters(comp)}::compute() {
if (shouldCompute())
{
<#if ComponentHelper.retainState(comp)>
  ${Identifier.getBehaviorImplName()}.restoreState();
</#if>
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
<#if ComponentHelper.retainState(comp)>
  ${Identifier.getBehaviorImplName()}.storeState();
</#if>
}
}