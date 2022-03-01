<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::checkPostconditions (
${compname}Input${generics} &input,
${compname}Result${generics} &result,
${compname}State${generics} &state,
${compname}State${generics} &state__at__pre)
{
for (auto post : postconditions)
{
post->apply (state, input, result, state__at__pre);
}
}