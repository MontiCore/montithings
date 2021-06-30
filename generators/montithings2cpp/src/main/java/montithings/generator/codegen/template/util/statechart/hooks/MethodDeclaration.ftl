<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  ${compname}StatechartState getStatechartState () const;
  void setStatechartState (${compname}StatechartState statechartState);
</#if>