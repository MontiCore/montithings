<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "className")}
<#include "/template/Preamble.ftl">

<#if ComponentHelper.hasStatechart(comp)>
  ${Utils.printTemplateArguments(comp)}
  ${compname}StatechartState ${className}${generics}::getStatechartState () const
  {
  return statechartState;
  }
</#if>