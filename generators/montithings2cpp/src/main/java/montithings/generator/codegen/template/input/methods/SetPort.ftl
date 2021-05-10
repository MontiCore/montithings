<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(tl::optional<Message<${type}>> element)
{
this->${port.getName()} = std::move(element);
<#if ComponentHelper.hasAgoQualification(comp, port)>
  auto now = std::chrono::system_clock::now();
  dequeOf__${port.getName()?cap_first}.push_back(std::make_pair(now, element.value()));
  cleanDequeOf${port.getName()?cap_first}(now);
</#if>
}