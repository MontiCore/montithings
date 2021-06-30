<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "className")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::setStatechartState(${compname}StatechartState statechartState)
  {
  ${className}::statechartState = statechartState;
  }
</#if>