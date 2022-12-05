<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${className}.h"
#include "${compname}.h"

<#if ComponentHelper.isDSLComponent(comp)>
  //DSL Recognized!!!
</#if>

${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.impl.Body", [comp, config, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}

