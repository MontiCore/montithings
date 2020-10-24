<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

#include "${compname}Input.h"
${Utils.printNamespaceStart(comp)}
<#if !Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.input.generateInputBody", [comp, compname, config])}
</#if>
${Utils.printNamespaceEnd(comp)}