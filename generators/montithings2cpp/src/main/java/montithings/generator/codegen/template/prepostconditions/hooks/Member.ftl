<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#include "/template/CompPreamble.ftl">
<#include "/template/TcPreamble.ftl">
<#assign generics = Utils.printFormalTypeParameters(comp)>

std::set${"<"}${compname}Precondition${generics}*${">"} preconditions;
std::set${"<"}${compname}Postcondition${generics}*${">"} postconditions;