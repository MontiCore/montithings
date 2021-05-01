<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("behavior", "comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
bool ${className}${Utils.printFormalTypeParameters(comp)}::shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}() {
  return
  <#list behavior.getNameList() as port>
  ${Identifier.getInterfaceName()}.getPort${port?cap_first}()->hasValue(this->uuid)
  <#sep>&&</#sep>
  </#list>
  ;
}