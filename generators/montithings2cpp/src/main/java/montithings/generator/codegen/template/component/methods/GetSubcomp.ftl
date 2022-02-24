<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className","subcomponent","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if dummyName4>
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