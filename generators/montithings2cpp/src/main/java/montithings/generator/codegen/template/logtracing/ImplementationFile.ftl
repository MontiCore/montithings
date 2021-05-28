<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}

<#include "/template/component/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${className}LogTraceObserver.h"

${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.logtracing.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}