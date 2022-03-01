<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::compute(){
// ensure there are no parallel compute() executions
if (remainingComputes > 0)
{
remainingComputes++;
return;
}
std::lock_guard${"<std::mutex>"} guard(computeMutex);

remainingComputes++;
while (remainingComputes > 0)
{
if (shouldCompute()) {

${tc.includeArgs("template.component.helper.ComputeInputs", [comp, config, false, "false"])}
${tc.includeArgs("template.logtracing.hooks.CheckInput", [comp, config])}
${tc.includeArgs("template.logtracing.hooks.CheckOutput", [comp, config])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "pre"])}

<#list ComponentHelper.getPortSpecificBehaviors(comp) as behavior>
  if (shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}(${Identifier.getInputName()}))
  {
  ${Identifier.getStateName()}__at__pre = ${Identifier.getStateName()};
    <#-- Different from atomic component we do not set a "result" here, because the composed
         components are not allowed to send messages on outgoing ports
     -->
  ${Identifier.getBehaviorImplName()}.compute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}(${Identifier.getInputName()});
  ${tc.includeArgs("template.component.helper.ComputeResults", [comp, config, true, className])}
  ${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}
  }
</#list>

<#if splittingModeDisabled>
    <#list comp.subComponents as subcomponent >
        this->${subcomponent.getName()}.compute();
    </#list>
</#if>


${tc.includeArgs("template.component.helper.ComputeResults", [comp, config, true, className])}
${tc.includeArgs("template.prepostconditions.hooks.Check", [comp, "post"])}

}
remainingComputes--;
}
}