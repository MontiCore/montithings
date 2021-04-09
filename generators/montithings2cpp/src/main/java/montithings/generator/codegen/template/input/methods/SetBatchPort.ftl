<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> vector)
{
this->${port.getName()} = std::move(vector);
}