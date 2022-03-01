<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign varName = var.getName()>
<#assign varType = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>
<#if ComponentHelper.hasAgoQualification(comp, var)>
  ${varType} agoGet${varName?cap_first} (const std::chrono::nanoseconds ago_time);
</#if>