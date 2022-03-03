<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/prepostconditions/helper/SpecificPreamble.ftl">

${Utils.printTemplateArguments(comp)}
bool
${className}${generics}::check (${compname}State${generics} ${Identifier.getStateName()},
${compname}Input${generics} ${Identifier.getInputName()}
<#if !isPrecondition>
  , ${compname}Result${generics} ${Identifier.getResultName()}
</#if>
  , ${compname}State${generics} ${Identifier.getStateName()}__at__pre
) const
{
<#if isPrecondition>
  return ${tc.includeArgs("template.prepostconditions.helper.printPreconditionCheck", [comp, statement])};
<#else>
  return ${tc.includeArgs("template.prepostconditions.helper.printPostconditionCheck", [comp, statement])};
</#if>
}