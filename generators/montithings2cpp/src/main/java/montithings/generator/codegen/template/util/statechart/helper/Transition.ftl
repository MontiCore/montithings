<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "transition")}
<#include "/template/Preamble.ftl">

<#assign sgeoies = StatechartHelper.getStates(comp)>
<#assign source = transition.getSourceName()>
<#assign target = transition.getTargetName()>
<#assign guardedPorts = StatechartHelper.getPortsInGuard(transition)>

/* implements ${PrettyPrinter.prettyprint(transition)} */
if (this->state.getStatechartState() == ${compname}StatechartState::${source})
{
<#if guardedPorts?size gt 0>
  if (
  <#list guardedPorts as currentPort>
    input.get${currentPort.getName()?cap_first} ().has_value () <#sep>&&</#sep>
  </#list>
  )
</#if>
{
<#if StatechartHelper.hasGuard(transition)>
  if (${ComponentHelper.printExpression(StatechartHelper.getGuard(transition))})
</#if>
{
// Execute transition
this->state.setStatechartState (${compname}StatechartState::${target});
// Action
<#if StatechartHelper.hasAction(transition)>
  ${ComponentHelper.printBlock(StatechartHelper.getAction(transition))}
</#if>
return ${Identifier.getResultName()};
}
}
}