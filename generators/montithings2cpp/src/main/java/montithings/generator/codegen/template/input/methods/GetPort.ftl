<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>

${Utils.printTemplateArguments(comp)}
tl::optional<${type}>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
{
return ${port.getName()}<#if config.getLogTracing().toString() == "ON">->second</#if>;
}


${tc.includeArgs("template.logtracing.hooks.GetInputUUIDDefinition", [comp, config, port])}