<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${generics}::apply (${compname}State${generics} &state,
${compname}Input${generics} &input
<#if !isPrecondition>, ${compname}Result${generics} &result</#if>)
{
if (check (state, input<#if !isPrecondition>, result</#if>))
{
if (isCatched ())
{
resolve (state, input<#if !isPrecondition>, result</#if>);
}
else
{
logError (state, input<#if !isPrecondition>, result</#if>);
}
}
}