<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

<#assign name = port.getName()?cap_first>
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>
<#assign cdeImportStatementOpt = ComponentHelper.getCDEReplacement(port, config)>

tl::optional<${type}> get${name}() const;
void set${name}(tl::optional<${type}>);

<#if cdeImportStatementOpt.isPresent()>
  <#assign cdType = cdeImportStatementOpt.get().getImportClass().toString()>
  tl::optional<${cdType}> get${name}Adap() const;
  void set${name}(${cdType});
</#if>

<#if ComponentHelper.hasAgoQualification(comp, port)>
  tl::optional<${type}> agoGet${name} (const std::chrono::nanoseconds ago_time);
</#if>