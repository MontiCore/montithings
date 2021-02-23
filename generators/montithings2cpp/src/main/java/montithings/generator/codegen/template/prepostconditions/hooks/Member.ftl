<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#include "/template/Preamble.ftl">
std::set${"<"}${compname}Precondition${generics}*${">"} preconditions;
std::set${"<"}${compname}Postcondition${generics}*${">"} postconditions;