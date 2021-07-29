<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign varName = var.getName()>
<#assign type = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>

${Utils.printTemplateArguments(comp)}
${type} ${className}${generics}::agoGet${varName?cap_first}(const std::chrono::nanoseconds ago_time)
{
auto now = std::chrono::system_clock::now();
for (auto it = dequeOf__${varName?cap_first}.crbegin(); it != dequeOf__${varName?cap_first}.crend(); ++it)
{
if (it->first < now - ago_time)
{
return it->second;
}
}
return dequeOf__${varName?cap_first}.front().second;
}