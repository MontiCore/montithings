<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  enum class ${compname}StatechartState {
    <#list StatechartHelper.getStates(comp) as currentState>
      ${currentState.getName()}
      <#sep>,</#sep>
    </#list>
  };
</#if>