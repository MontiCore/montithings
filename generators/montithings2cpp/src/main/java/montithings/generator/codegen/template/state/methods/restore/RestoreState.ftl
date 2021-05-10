<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::restoreState ()
{
// read in file
std::ifstream storeFile(this->instanceName + ".json");
std::string content;
while(std::getline(storeFile, content))
;
return restoreState (content);
}