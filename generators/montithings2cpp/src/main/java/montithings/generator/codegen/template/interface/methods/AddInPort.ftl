<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "name", "type", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/interface/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::addInPort${name?cap_first}(Port<${type}>* port){
${name}->getInport ()->addManagedPort (port);
}