<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/prepostconditions/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
bool
${className}${generics}::isCatched ()
{
return false;
}