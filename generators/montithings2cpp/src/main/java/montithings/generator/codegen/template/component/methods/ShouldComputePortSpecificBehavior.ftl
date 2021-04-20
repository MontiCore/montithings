<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("behavior", "comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
bool ${className}${Utils.printFormalTypeParameters(comp)}::shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}(${compname}Input ${Identifier.getInputName()}) {
  return
  <#list behavior.getNameList() as port>
  ${Identifier.getInputName()}.get${port?cap_first}().has_value()<#sep>&&</#sep>
  </#list>
  ;
}