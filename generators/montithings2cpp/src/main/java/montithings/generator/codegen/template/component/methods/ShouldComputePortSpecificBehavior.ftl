<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("behavior", "comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
bool ${className}${Utils.printFormalTypeParameters(comp)}::shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}
(<#if !comp.isAtomic()>${compname}Input${generics}& ${Identifier.getInputName()}</#if>) {
  return
    <#list behavior.getNameList() as port>
      <#if comp.isAtomic()>
        ${Identifier.getInterfaceName()}.getPort${port?cap_first}()->hasValue(this->uuid)
      <#else>
        ${Identifier.getInputName()}.get${port?cap_first}().has_value()
      </#if>
      <#sep>&&</#sep>
    </#list>
  ;
}