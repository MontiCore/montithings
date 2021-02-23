<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/SpecificPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${generics}::resolve (${compname}State${generics} &state,
${compname}Input${generics} &${Identifier.getInputName()}
<#if !isPrecondition>, ${compname}Result${generics} &${Identifier.getResultName()}</#if>)
{
<#if catch.isPresent()>
    ${ComponentHelper.printJavaBlock(catch.get().handler)}
</#if>
}