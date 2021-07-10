<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}

<#include "/template/logtracing/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${compname}.h"
#include "${className}.h"

${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.logtracing.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}