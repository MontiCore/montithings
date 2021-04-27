<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">
    
#include "${className}.h"
${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.result.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}