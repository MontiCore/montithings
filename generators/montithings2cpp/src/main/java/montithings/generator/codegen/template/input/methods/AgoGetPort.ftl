<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/input/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
tl::optional<${TypesPrinter.getRealPortCppTypeString(comp, port, config)}> ${className}${Utils.printFormalTypeParameters(comp, false)}::agoGet${port.getName()?cap_first}(const std::chrono::nanoseconds ago_time)
{
if(dequeOf__${port.getName()?cap_first}.empty()){
return tl::nullopt;
}
auto now = std::chrono::system_clock::now();
for (auto it = dequeOf__${port.getName()?cap_first}.crbegin(); it != dequeOf__${port.getName()?cap_first}.crend(); ++it)
{
if (it->first < now - ago_time)
{
return tl::make_optional(it->second);
}
}
return tl::make_optional(dequeOf__${port.getName()?cap_first}.front().second);
}