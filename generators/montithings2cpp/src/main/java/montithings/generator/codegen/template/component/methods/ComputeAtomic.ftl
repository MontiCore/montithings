<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className", "computeName")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::compute${computeName}() {
// ensure there are no parallel compute() executions
<#if !ComponentHelper.isEveryBlock(computeName, comp)>
  if (remainingComputes > 0)
  {
  remainingComputes++;
  return;
  }
</#if>
std::lock_guard${"<std::mutex>"} guard(compute${computeName}Mutex);

<#if !ComponentHelper.isEveryBlock(computeName, comp)>
  remainingComputes++;
  while (remainingComputes > 0 && !(this->stopSignalReceived))
  {
  if (shouldCompute())
  {
</#if>

${compname}Result${Utils.printFormalTypeParameters(comp)} ${Identifier.getResultName()};

${tc.includeArgs("template.logtracing.hooks.CheckOutput", [comp, config])}

<#if ComponentHelper.isEveryBlock(computeName, comp)>
  ${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, "false"])}
  ${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}
  ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};
  
  ${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${computeName}(${Identifier.getInputName()});

  ${tc.includeArgs("template.logtracing.hooks.CheckInput", [comp, config])}
  if (timeMode == TIMESYNC) {
  ${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
  setResult(${Identifier.getResultName()});
  }
<#else>
  <#list ComponentHelper.getInitBehaviorsWithoutBehaviors(comp) as initBehavior>
  if (shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, initBehavior)}() && !initialized${ComponentHelper.getPortSpecificInitBehaviorName(comp, initBehavior)})
  {
  ${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, initBehavior])}
  ${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}
  ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};
  ${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.init${ComponentHelper.getPortSpecificInitBehaviorName(comp, initBehavior)}(${Identifier.getInputName()});
  initialized${ComponentHelper.getPortSpecificInitBehaviorName(comp, initBehavior)} = true;
  ${tc.includeArgs("template.logtracing.hooks.CheckInput", [comp, config])}
  }
  <#sep>else </#sep>
  </#list>
  <#list ComponentHelper.getPortSpecificBehaviors(comp) as behavior>
  if (shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}())
  {
  ${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, behavior])}
  ${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}
  ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};
  <#if ComponentHelper.hasInitBehavior(comp, behavior)>
  if (!initialized${ComponentHelper.getInitBehaviorName(comp, behavior)}) {
    ${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.init${ComponentHelper.getInitBehaviorName(comp, behavior)}(${Identifier.getInputName()});
    initialized${ComponentHelper.getInitBehaviorName(comp, behavior)} = true;
  }
  else {
    ${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}(${Identifier.getInputName()});
  }
  <#else>
  ${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}(${Identifier.getInputName()});
  </#if>
  ${tc.includeArgs("template.logtracing.hooks.CheckInput", [comp, config])}
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
    ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};
    ${Identifier.getResultName()} = ${Identifier.getBehaviorImplName()}.compute${computeName}(${Identifier.getInputName()});
    ${tc.includeArgs("template.logtracing.hooks.CheckInput", [comp, config])}
    if (timeMode == TIMESYNC) {
    ${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
    setResult(${Identifier.getResultName()});
    }
    <#if ComponentHelper.hasPortSpecificBehavior(comp)>}</#if>
  </#if>
</#if>

<#if ComponentHelper.retainState(comp)>
  json json__state = ${Identifier.getStateName()}.serializeState ();
  <#if brokerIsMQTT>
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

<#if !ComponentHelper.isEveryBlock(computeName, comp)>
}
remainingComputes--;
}
</#if>
}