<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">


<#list comp.getSubComponents() as subcomponent>
    std::string ${comp.getName()}::get${subcomponent.getName()?cap_first}IP(){
    return subcomp${subcomponent.getName()?cap_first}IP;
    }
    void ${comp.getName()}::set${subcomponent.getName()?cap_first}IP(std::string ${subcomponent.getName()}IP){
    subcomp${subcomponent.getName()?cap_first}IP = ${subcomponent.getName()}IP;
    }
</#list>