<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "name", "type", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::removeOutPort${name?cap_first}(Port<${type}>* port){
${name}->getOutport ()->removeManagedPort (port);
}