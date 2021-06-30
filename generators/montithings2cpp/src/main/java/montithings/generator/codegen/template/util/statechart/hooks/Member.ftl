<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  <#assign initial = StatechartHelper.getInitialState(comp).getName()>
  ${compname}StatechartState statechartState = ${compname}StatechartState::${initial};
</#if>