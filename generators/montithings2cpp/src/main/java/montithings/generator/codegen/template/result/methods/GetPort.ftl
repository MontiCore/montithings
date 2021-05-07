<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">

<#assign name = port.getName()>
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>
<#assign typeWrapped = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

${Utils.printTemplateArguments(comp)}
tl::optional<${type}>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${name?cap_first}() const
{
return ${name};
}

<#if config.getLogTracing().toString() == "ON">
    tl::optional<${typeWrapped}>
    ${className}${Utils.printFormalTypeParameters(comp, false)}::get${name?cap_first}Wrapped() const
    {
        ${tc.includeArgs("template.logtracing.hooks.PrepareResult", [comp, config, port])}
        return ${name}Wrapped;
    }
</#if>