<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/prepostconditions/helper/SpecificPreamble.ftl">

${Utils.printTemplateArguments(comp)}
std::string
${className}${generics}::toString () const
{
return R"('${PrettyPrinter.prettyprint(statement.guard)}')";
}