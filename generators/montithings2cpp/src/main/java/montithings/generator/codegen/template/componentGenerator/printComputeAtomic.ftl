<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className", "computeName")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::compute${computeName}() {
// ensure there are no parallel compute() executions
std::lock_guard${"<std::mutex>"} guard(compute${computeName}Mutex);

if (shouldCompute())
{
${tc.includeArgs("template.componentGenerator.printComputeInputs", [comp, compname, false])}
${compname}Result${Utils.printFormalTypeParameters(comp)} ${Identifier.getResultName()};
${compname}State ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};

${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}
${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${computeName}(${Identifier.getInputName()});
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}

setResult(${Identifier.getResultName()});
<#if ComponentHelper.retainState(comp)>
  json json__state = ${Identifier.getStateName()}.serializeState ();
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
    ${Identifier.getStateName()}.publishState (json__state);
    }
  </#if>
  ${Identifier.getStateName()}.storeState (json__state);
</#if>
}
}