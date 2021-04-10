<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign type = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>
<#assign varName = var.getName()>

${Utils.printTemplateArguments(comp)}
${type} ${className}${generics}::get${varName?cap_first}() const
{
return ${varName};
}