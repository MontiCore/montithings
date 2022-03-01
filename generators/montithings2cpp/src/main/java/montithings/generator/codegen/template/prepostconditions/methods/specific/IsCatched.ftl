<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/helper/SpecificPreamble.ftl">

${Utils.printTemplateArguments(comp)}
bool
${className}${generics}::isCatched ()
{
<#if catch.isPresent()>
  return true;
<#else>
  return false;
</#if>
}