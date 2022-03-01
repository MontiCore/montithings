<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign type = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>
<#assign varName = var.getName()>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::set${varName?cap_first}(${type} ${varName})
{
${className}${generics}::${varName} = ${varName};
<#if ComponentHelper.isArcField(var) && ComponentHelper.hasAgoQualification(comp, var)>
  auto now = std::chrono::system_clock::now();
  dequeOf__${varName?cap_first}.push_back(std::make_pair(now, ${varName}));
  cleanDequeOf${varName?cap_first}(now);
</#if>
}