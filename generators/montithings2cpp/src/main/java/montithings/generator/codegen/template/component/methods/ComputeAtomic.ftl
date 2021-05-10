<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className", "computeName")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::compute${computeName}() {
// ensure there are no parallel compute() executions
std::lock_guard${"<std::mutex>"} guard(compute${computeName}Mutex);

if (shouldCompute())
{
${compname}Result${Utils.printFormalTypeParameters(comp)} ${Identifier.getResultName()};
${compname}State${Utils.printFormalTypeParameters(comp)} ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};

<#if ComponentHelper.isEveryBlock(computeName, comp)>
${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, "false"])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}
${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${computeName}(${Identifier.getInputName()});
if (timeMode == TIMESYNC) {
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
setResult(${Identifier.getResultName()});
}
<#else>
<#list ComponentHelper.getPortSpecificBehaviors(comp) as behavior>
if (shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}())
{
${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, behavior])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}
${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}(${Identifier.getInputName()});
if (timeMode == TIMESYNC) {
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
setResult(${Identifier.getResultName()});
}
}
<#sep>else </#sep>
</#list>
<#if ComponentHelper.hasGeneralBehavior(comp) || !ComponentHelper.hasPortSpecificBehavior(comp)>
<#if ComponentHelper.hasPortSpecificBehavior(comp)>else {</#if>
${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, "false"])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}
${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${computeName}(${Identifier.getInputName()});
if (timeMode == TIMESYNC) {
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
setResult(${Identifier.getResultName()});
}
<#if ComponentHelper.hasPortSpecificBehavior(comp)>}</#if>
</#if>
</#if>


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