${tc.signature("comp","config")}
<#list comp.getSubComponents() as subcomponent>
    std::string ${comp.getName()}::get${subcomponent.getName()?cap_first}IP(){
    return subcomp${subcomponent.getName()?cap_first}IP;
    }
    void ${comp.getName()}::set${subcomponent.getName()?cap_first}IP(std::string ${subcomponent.getName()}IP){
    subcomp${subcomponent.getName()?cap_first}IP = ${subcomponent.getName()}IP;
    }
</#list>