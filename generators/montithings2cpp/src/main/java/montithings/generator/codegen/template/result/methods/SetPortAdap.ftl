<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">

void
${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(${cdeImportStatementOpt.get().getImportClass().toString()} element)
{
${adapterName?cap_first} ${adapterName?uncap_first};
this->${port.getName()} = ${adapterName?uncap_first}.convert${cdSimpleName}(element);
}