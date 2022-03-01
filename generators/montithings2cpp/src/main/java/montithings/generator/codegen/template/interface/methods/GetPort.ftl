<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "name", "type", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/interface/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
InOutPort<${type}>* ${className}${Utils.printFormalTypeParameters(comp)}::getPort${name?cap_first}(){
return ${name};
}