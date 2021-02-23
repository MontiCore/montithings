<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/prepostconditions/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${className}${generics}::${className} (const std::string &instanceName)
: instanceName (instanceName)
{
}