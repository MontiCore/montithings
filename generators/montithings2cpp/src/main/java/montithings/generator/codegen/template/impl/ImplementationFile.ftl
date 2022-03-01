<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/impl/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${className}.h"
#include "${compname}.h"

${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.impl.Body", [comp, config, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}

