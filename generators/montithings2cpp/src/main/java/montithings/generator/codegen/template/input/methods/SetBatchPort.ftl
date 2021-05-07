<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>
<#assign type = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(std::vector<${type}> vector)
{
this->${port.getName()} = std::move(vector);
}