<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(std::vector<Message<${type}>> vector)
{
this->${port.getName()} = std::move(vector);
}