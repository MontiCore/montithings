<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/input/helper/AdapterPreamble.ftl">

tl::optional<${cdeImportStatementOpt.get().getImportClass().toString()}>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}Adap() const
{
if (!get${port.getName()?cap_first}().has_value())
{
return {};
}
${adapterName?cap_first} ${adapterName?uncap_first};
return ${adapterName?uncap_first}.convert${cdSimpleName}(*get${port.getName()?cap_first}());
}