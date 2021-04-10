<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "name", "type", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
double ${className}${Utils.printFormalTypeParameters(comp)}::getPort${name?cap_first}ConversionFactor(){
return ${name}ConversionFactor;
}