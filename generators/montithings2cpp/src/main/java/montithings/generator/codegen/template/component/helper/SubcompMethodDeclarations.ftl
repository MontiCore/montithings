<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">


<#list comp.getSubComponents() as subcomponent>
    std::string get${subcomponent.getName()?cap_first}IP();
    void set${subcomponent.getName()?cap_first}IP(std::string ${subcomponent.getName()}IP);
</#list>