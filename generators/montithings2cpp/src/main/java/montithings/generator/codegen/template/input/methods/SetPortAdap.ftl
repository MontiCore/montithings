<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/input/helper/AdapterPreamble.ftl">

<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>

void
${className}${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(${cdeImportStatementOpt.get().getImportClass().toString()} element)
{
${adapterName?cap_first} ${adapterName?uncap_first};
Message<${type}> message(${adapterName?uncap_first}.convert${cdSimpleName}(element));
this->${port.getName()} = message;
}