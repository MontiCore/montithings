<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${compname}${prefix}condition.h"

${Utils.printNamespaceStart(comp)}

<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.prepostconditions.GeneralBody", [comp, config, isPrecondition, existsHWC])}
</#if>

${Utils.printNamespaceEnd(comp)}
