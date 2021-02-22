<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#assign compname = comp.getName()>
std::set${"<"}${compname}Precondition*${">"} preconditions;
std::set${"<"}${compname}Postcondition*${">"} postconditions;