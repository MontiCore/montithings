<#-- (c) https://github.com/MontiCore/monticore -->
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#--package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentInstanceSymbol
import arcbasis._symboltable.ComponentTypeSymbol
import java.util.HashSet
import java.util.Set
import montithings.generator.codegen.ConfigParams
import montithings.generator.helper.ComponentHelper-->


  <#macro printVars comp config>
    <#if config.getSplittingMode().toString() == "OFF">
      <#list comp.getSubComponents() as subcomponent>
        <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
        ${Utils.printPackageNamespace(comp, subcomponent)}${type} ${subcomponent.getName()};
      </#list>
    <#else>
      <#list comp.getSubComponents() as subcomponent >
 std::string subcomp${subcomponent.getName()?cap_first}IP;
 </#list>
    </#if>
</#macro>

  <#macro printMethodDeclarations comp config>
    <#list comp.getSubComponents() as subcomponent>
          std::string get${subcomponent.getName()?cap_first}IP();
          void set${subcomponent.getName()?cap_first}IP(std::string ${subcomponent.getName()}IP);
    </#list>
</#macro>

  <#macro printMethodDefinitions comp config>
    <#list comp.getSubComponents() as subcomponent>
        std::string ${comp.getName()}::get${subcomponent.getName()?cap_first}IP(){
          return subcomp${subcomponent.getName()?cap_first}IP;
        }
        void ${comp.getName()}::set${subcomponent.getName()?cap_first}IP(std::string ${subcomponent.getName()}IP){
          subcomp${subcomponent.getName()?cap_first}IP = ${subcomponent.getName()}IP;
        }
    </#list>
</#macro>

  <#macro printInitializerList comp config>
      <#list comp.subComponents as subcomponent>
        ${subcomponent.getName()}( "${subcomponent.getName()}"
          <#if config.getSplittingMode().toString() == "OFF">
          <#list ComponentHelper.getParamValues(subcomponent) as param >
<#sep>,
param
 </#list>
          </#if>)<#sep>,
      </#list>
</#macro>

