<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign className = compname + "Adapter">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#include "${className}.h"

${tc.includeArgs("template.adapter.printNamespaceStart", [packageName])}

${tc.includeArgs("template.adapter.printNamespaceEnd", [packageName])}
