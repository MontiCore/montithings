<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  // set statechart state
  statechartState = static_cast<${compname}StatechartState>(jsonToData<int>(state["statechartState"]));
</#if>