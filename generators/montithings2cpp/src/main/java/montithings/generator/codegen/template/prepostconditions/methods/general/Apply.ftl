<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${generics}::apply (${compname}State${generics} &state,
${compname}Input${generics} &input
<#if !isPrecondition>
  , ${compname}Result${generics} &result
</#if>
  , ${compname}State${generics} &state__at__pre
)
{
if (check (state, input<#if !isPrecondition>, result</#if>, state__at__pre))
{
if (isCatched ())
{
resolve (state, input<#if !isPrecondition>, result</#if>, state__at__pre);
}
else
{
logError (state, input<#if !isPrecondition>, result</#if>, state__at__pre);
}
}
}