<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign className = comp.getName() + "State">
<#if existsHWC>
    <#assign className += "TOP">
</#if>


#include "${className}.h"
${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.state.generateStateBody", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}