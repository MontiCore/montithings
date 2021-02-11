<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::compute() {
// ensure there are no parallel compute() executions
std::lock_guard${"<std::mutex>"} guard(computeMutex);

if (shouldCompute())
{
${tc.includeArgs("template.componentGenerator.printComputeInputs", [comp, compname, false])}
${compname}Result${Utils.printFormalTypeParameters(comp)} ${Identifier.getResultName()};
<#list comp.incomingPorts as port>
<#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
</#list>
<#list ComponentHelper.getArcFieldVariables(comp) as field>
  ${ComponentHelper.printCPPTypeName(field.getType())} ${field.getName()}__at__pre = ${field.getName()};
</#list>
${tc.includeArgs("template.componentGenerator.printPreconditionsCheck", [comp, compname])}
${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute(${Identifier.getInputName()});
<#list ComponentHelper.getVariablesAndParameters(comp) as var>
  <#assign varName = var.getName()>
  ${varName} = ${Identifier.getBehaviorImplName()}.get${varName?cap_first}();
</#list>
<#list comp.getOutgoingPorts() as port>
<#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
</#list>
${tc.includeArgs("template.componentGenerator.printPostconditionsCheck", [comp, compname])}
setResult(${Identifier.getResultName()});
<#if ComponentHelper.retainState(comp)>
  json state = ${Identifier.getBehaviorImplName()}.serializeState ();
  <#if config.getMessageBroker().toString() == "MQTT">
    <#-- if there's no incoming ports, we have no chance of replaying and need
         to store every message. If there's at least one incoming port it is sufficient
         to save every couple of messages and replay everything after last save -->
    <#if comp.getIncomingPorts()?size gt 0>
      static int computeCounter = 0;
      computeCounter++;
      computeCounter %= 5;
      if (computeCounter == 0)
    </#if>
    {
    ${Identifier.getBehaviorImplName()}.publishState (state);
    }
  </#if>
  ${Identifier.getBehaviorImplName()}.storeState (state);
</#if>
}
}