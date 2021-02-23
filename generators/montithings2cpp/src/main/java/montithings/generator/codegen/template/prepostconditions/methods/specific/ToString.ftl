<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/SpecificPreamble.ftl">

${Utils.printTemplateArguments(comp)}
std::string
${className}${generics}::toString () const
{
return R"('${PrettyPrinter.prettyprint(statement.guard)}')";
}