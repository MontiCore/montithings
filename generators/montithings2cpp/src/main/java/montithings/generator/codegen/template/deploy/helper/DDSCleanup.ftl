<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if !(splittingModeDisabled)>
free(ddsArgv[0]);
free(ddsArgv[2]);
</#if>

<#if splittingModeIsDistributed>
  free(ddsArgv[4]);
</#if>