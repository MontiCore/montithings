<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">

<#assign name = port.getName()>
<#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp, false)}::set${name?cap_first}(tl::optional<${type}> ${name})
{
this->${name} = ${name};
<#if ComponentHelper.hasAgoQualification(comp, port)>
  auto now = std::chrono::system_clock::now();
  dequeOf__${name?cap_first}.push_back(std::make_pair(now, ${name}.value()));
  cleanDequeOf${name?cap_first}(now);
</#if>
}