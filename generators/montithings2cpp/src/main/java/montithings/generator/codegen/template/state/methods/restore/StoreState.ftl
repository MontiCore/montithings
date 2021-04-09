<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::storeState (json state)
{
// empty file
std::ofstream storeFile;
storeFile.open (this->instanceName + ".json", std::ofstream::out | std::ofstream::trunc);
storeFile.close ();

// store state
storeFile.open (this->instanceName + ".json", std::ios_base::app | std::ios_base::out);
storeFile << state.dump ();
storeFile.close ();
}