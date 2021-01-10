<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

free(ddsArgv[0]);
free(ddsArgv[2]);
<#if config.getSplittingMode().toString() == "DISTRIBUTED">
  free(ddsArgv[4]);
</#if>