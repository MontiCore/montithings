<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className", "interfaceName", "ports")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#-- Types automatically generated for interfaces are prefixes with "Co" -->
<#assign coName = TypesHelper.getComponentTypePrefix() + interfaceName?cap_first>

${Utils.printTemplateArguments(comp)}
std::string
${className}${Utils.printFormalTypeParameters(comp)}::getConnectionString${coName} () const
{
${coName}::${coName} compInterface;

<#list ports as port>
  {
  PortLink link (instanceName  + ".${port.getName()}");
  compInterface.set${port.getName()?cap_first} (link);
  }
</#list>

Message${"<"}${coName}::${coName}${">"} msg (compInterface);
return dataToJson (msg);
}