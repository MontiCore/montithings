<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if dummyName8>
  <#list comp.getSubComponents() as subcomponent>
      <#if dummyName4>
        <#assign type = subcomponent.getGenericType().getName()>
      <#else>
        <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
      </#if>
      ${Utils.printPackageNamespace(comp, subcomponent)}${type} ${subcomponent.getName()};
  </#list>
<#elseif brokerDisabled>
  <#if ComponentHelper.shouldIncludeSubcomponents(comp, config)>
    <#list comp.getSubComponents() as subcomponent >
      std::string subcomp${subcomponent.getName()?cap_first}IP;
    </#list>
  </#if>
</#if>


