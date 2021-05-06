<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">


<#if ComponentHelper.hasBehavior(comp)>
  ${tc.includeArgs("template.impl.methods.GetInitialValues", [comp, config, existsHWC])}
  ${tc.includeArgs("template.impl.methods.Compute", [comp, config, existsHWC])}
</#if>

<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
  ${tc.includeArgs("template.impl.methods.ComputeEveryBlock", [everyBlock, comp, config, existsHWC])}
</#list>

<#list ComponentHelper.getPortSpecificBehaviors(comp) as behavior>
  ${tc.includeArgs("template.impl.methods.ComputePortSpecificBehavior", [behavior, comp, config, existsHWC])}
</#list>