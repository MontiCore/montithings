<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  state["statechartState"] = dataToJson (static_cast<int>(statechartState));
</#if>