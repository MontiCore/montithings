<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "useWsPorts", "existsHWC")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#include "${className}.h"
#include ${"<regex>"}
${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.component.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}