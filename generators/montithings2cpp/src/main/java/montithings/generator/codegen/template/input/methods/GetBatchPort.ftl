<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
{
return ${port.getName()};
}