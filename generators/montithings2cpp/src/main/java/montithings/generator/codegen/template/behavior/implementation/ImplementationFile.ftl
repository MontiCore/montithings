<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign className = compname + "Impl">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#include "${className}.h"

${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.behavior.implementation.generateImplementationBody", [comp, compname, className])}
</#if>
${Utils.printNamespaceEnd(comp)}

