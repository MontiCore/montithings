<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${compname}Interface.h"

${Utils.printNamespaceStart(comp)}

<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.interface.Body", [comp, config, existsHWC])}
</#if>

${Utils.printNamespaceEnd(comp)}
