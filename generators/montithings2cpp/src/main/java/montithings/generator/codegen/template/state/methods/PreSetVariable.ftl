<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign type = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>
<#assign varName = var.getName()>

${Utils.printTemplateArguments(comp)}
${type} ${className}${generics}::preSet${varName?cap_first}(${type} ${varName})
{
set${varName?cap_first}(${varName});
return get${varName?cap_first}();
}