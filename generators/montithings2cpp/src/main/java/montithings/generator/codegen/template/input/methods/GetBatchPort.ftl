<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

${Utils.printTemplateArguments(comp)}
std::vector<Message<${type}>>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
{
return ${port.getName()};
}