<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign generics = Utils.printFormalTypeParameters(comp)>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::storeState ()
{
// serialize state
json state;
<#list ComponentHelper.getFields(comp) as variable>
state["${variable.getName()}"] = dataToJson (${variable.getName()});
</#list>

// empty file
std::ofstream storeFile;
storeFile.open (this->instanceName + ".json", std::ofstream::out | std::ofstream::trunc);
storeFile.close ();

// store state
storeFile.open (this->instanceName + ".json", std::ios_base::app | std::ios_base::out);
storeFile << state.dump();
storeFile.close ();
}


${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::restoreState ()
{
try
{
// read in file
std::ifstream storeFile(this->instanceName + ".json");
json state;
storeFile >> state;

// set state
<#list ComponentHelper.getFields(comp) as variable>
  ${variable.getName()} = jsonToData${"<"}${ComponentHelper.printCPPTypeName(variable.getType())}${">"}(state["${variable.getName()}"]);
</#list>

return true;
}
catch (nlohmann::detail::parse_error &error)
{
std::cout << "Could not restore state." << std::endl;
return false;
}


}