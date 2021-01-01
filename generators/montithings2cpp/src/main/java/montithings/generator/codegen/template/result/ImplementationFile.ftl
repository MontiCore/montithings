<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign className = compname + "Result">
<#if existsHWC>
    <#assign className += "TOP">
</#if>    
    
    
#include "${className}.h"
${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.result.generateResultBody", [comp, compname, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}