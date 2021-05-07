<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>
<#assign type = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

${Utils.printTemplateArguments(comp)}
std::vector<${type}>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
{
return ${port.getName()};
}