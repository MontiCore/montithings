<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/prepostconditions/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${generics}::apply (${compname}State${generics} &state,
${compname}Input${generics} &input
<#if !isPrecondition>
  , ${compname}Result${generics} &result
  , ${compname}State${generics} &state__at__pre
</#if>)
{
if (check (state, input<#if !isPrecondition>, result, state__at__pre</#if>))
{
if (isCatched ())
{
resolve (state, input<#if !isPrecondition>, result, state__at__pre</#if>);
}
else
{
logError (state, input<#if !isPrecondition>, result, state__at__pre</#if>);
}
}
}