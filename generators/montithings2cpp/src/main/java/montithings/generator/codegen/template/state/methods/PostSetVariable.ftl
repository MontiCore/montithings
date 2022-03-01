<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign type = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>
<#assign varName = var.getName()>

${Utils.printTemplateArguments(comp)}
${type} ${className}${generics}::postSet${varName?cap_first}(${type} ${varName})
{
${type} beforeValue = get${varName?cap_first}();
set${varName?cap_first}(${varName});
return beforeValue;
}