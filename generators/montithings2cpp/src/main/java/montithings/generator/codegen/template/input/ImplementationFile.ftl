<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${className}.h"
${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.input.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}