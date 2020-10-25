<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void ${compname}${Utils.printFormalTypeParameters(comp)}::start(){
threads.push_back(std::thread{&${compname}${Utils.printFormalTypeParameters(comp)}::run, this});
}