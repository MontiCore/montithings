<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if splittingModeDisabled || ComponentHelper.shouldIncludeSubcomponents(comp, config)>
  <#list comp.getSubComponents() as subcomponent>
      <#if Utils.getGenericParameters(comp)?seq_contains(subcomponent.getGenericType().getName())>
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


