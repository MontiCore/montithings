<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

<#assign name = port.getName()?cap_first>
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>
<#assign type = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

std::vector<${type}> get${name}() const;
void add${name}Element(tl::optional<${type}>);
void set${name}(std::vector<${type}> vector);