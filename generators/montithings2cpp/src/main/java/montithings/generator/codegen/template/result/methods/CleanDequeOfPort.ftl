<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::cleanDequeOf${port.getName()?cap_first}(std::chrono::time_point<std::chrono::system_clock> now)
{
while (dequeOf__${port.getName()?cap_first}.size() > 1 && dequeOf__${port.getName()?cap_first}.at (1).first < now - highestAgoOf__${port.getName()?cap_first})
{
dequeOf__${port.getName()?cap_first}.pop_front ();
}
}