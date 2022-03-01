<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("var", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/state/helper/GeneralPreamble.ftl">

<#assign varName = var.getName()>
<#assign varType = TypesPrinter.printCPPTypeName(var.getType(), comp, config)>

${varType} get${varName?cap_first} () const;
void set${varName?cap_first} (${varType} ${varName});
${varType} preSet${varName?cap_first} (${varType} ${varName});
${varType} postSet${varName?cap_first} (${varType} ${varName});