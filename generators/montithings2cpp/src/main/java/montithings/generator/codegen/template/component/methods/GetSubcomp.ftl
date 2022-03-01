<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className","subcomponent","config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if Utils.getGenericParameters(comp)?seq_contains(subcomponent.getGenericType().getName())>
  <#assign type = subcomponent.getGenericType().getName()>
<#else>
  <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
</#if>

${Utils.printTemplateArguments(comp)}
${Utils.printPackageNamespace(comp, subcomponent)}${type}*
${className}${Utils.printFormalTypeParameters(comp)}::getSubcomp__${subcomponent.getName()?cap_first} ()
{
return &${subcomponent.getName()};
}