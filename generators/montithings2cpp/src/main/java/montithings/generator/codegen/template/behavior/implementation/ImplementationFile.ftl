<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>

#include "${compname}Impl<#if existsHWC>TOP</#if>.h"
${Utils.printNamespaceStart(comp)}
<#if !comp.hasTypeParameter()>
    ${tc.includeArgs("template.behavior.implementation.generateImplementationBody", [comp, compname, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}

