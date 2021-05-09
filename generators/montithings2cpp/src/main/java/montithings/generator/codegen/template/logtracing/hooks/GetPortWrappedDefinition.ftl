<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">
<#assign name = port.getName()?cap_first>
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>
<#assign typeWrapped = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

<#if config.getLogTracing().toString() == "ON">
    tl::optional<${typeWrapped}>
    ${className}${Utils.printFormalTypeParameters(comp, false)}::get${name?cap_first}Wrapped(sole::uuid id) const
    {
        std::pair${"<"}sole::uuid, tl::optional${"<"}${ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)}${">>"} ${port.getName()}Wrapped = std::make_pair(id, get${name?cap_first}());

        return ${port.getName()}Wrapped;
    }
</#if>