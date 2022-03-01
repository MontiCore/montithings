<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "name", "type", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/interface/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::setPort${name?cap_first}ConversionFactor(double factor){
${name}ConversionFactor = factor;
}