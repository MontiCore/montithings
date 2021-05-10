<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp, false)}::add${port.getName()?cap_first}Element(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> element)
{
if (element)
{
${port.getName()}.push_back(element.value());
}
}