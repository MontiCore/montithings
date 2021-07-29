<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign varName = var.getName()>
<#assign type = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::cleanDequeOf${varName?cap_first}(std::chrono::time_point<std::chrono::system_clock> now)
{
while (dequeOf__${varName?cap_first}.size() > 1 && dequeOf__${varName?cap_first}.at (1).first < now - highestAgoOf__${varName?cap_first})
{
dequeOf__${varName?cap_first}.pop_front ();
}
}