<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>

${Utils.printTemplateArguments(comp)}
tl::optional<${type}>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
{
return ${port.getName()}.getPayload();
}

${Utils.printTemplateArguments(comp)}
sole::uuid
${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}Uuid()
{
  return ${port.getName()}.getUuid();
}