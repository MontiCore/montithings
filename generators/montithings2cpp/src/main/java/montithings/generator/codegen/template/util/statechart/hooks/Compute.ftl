<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  <#list StatechartHelper.getTransitions(comp) as transition>
    ${tc.includeArgs("template.util.statechart.helper.Transition", [comp, config, transition])}
  </#list>
</#if>