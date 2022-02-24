<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if !(config.getSplittingMode().toString() == "OFF")>
free(ddsArgv[0]);
free(ddsArgv[2]);
</#if>

<#if config.getSplittingMode().toString() == "DISTRIBUTED">
  free(ddsArgv[4]);
</#if>