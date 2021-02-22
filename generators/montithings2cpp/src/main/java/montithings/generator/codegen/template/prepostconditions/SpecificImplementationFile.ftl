<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign PrettyPrinter = tc.instantiate("montithings._visitor.MontiThingsPrettyPrinterDelegator")>
<#assign compname = comp.getName()>
<#assign prefix = "">
<#if isPrecondition>
    <#assign prefix = "Pre">
<#else>
    <#assign prefix = "Post">
</#if>
<#assign className = compname + prefix + "condition" + number>
<#if existsHWC>
    <#assign className += "TOP">
</#if>
<#assign catch = ComponentHelper.getCatch(comp, statement)>

#include "${compname}${prefix}condition${number}.h"
#include ${"<regex>"}

${Utils.printNamespaceStart(comp)}

bool
${className}::check (${compname}State state, ${compname}Input ${Identifier.getInputName()}
<#if !isPrecondition>, ${compname}Result ${Identifier.getResultName()}</#if>) const
{
<#if isPrecondition>
  return ${tc.includeArgs("template.prepostconditions.helper.printPreconditionCheck", [comp, statement])};
<#else>
  return ${tc.includeArgs("template.prepostconditions.helper.printPostconditionCheck", [comp, statement])};
</#if>
}

void
${className}::resolve (${compname}State &state, ${compname}Input &${Identifier.getInputName()}
<#if !isPrecondition>, ${compname}Result &${Identifier.getResultName()}</#if>)
{
<#if catch.isPresent()>
  ${ComponentHelper.printJavaBlock(catch.get().handler)}
</#if>
}

std::string
${className}::toString () const
{
return R"('${PrettyPrinter.prettyprint(statement.guard)}')";
}

bool
${className}::isCatched ()
{
<#if catch.isPresent()>
  return true;
<#else>
  return false;
</#if>
}

${Utils.printNamespaceEnd(comp)}

