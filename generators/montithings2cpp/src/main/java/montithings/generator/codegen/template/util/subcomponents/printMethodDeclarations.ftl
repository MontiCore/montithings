${tc.signature("comp","config")}
<#list comp.getSubComponents() as subcomponent>
    std::string get${subcomponent.getName()?cap_first}IP();
    void set${subcomponent.getName()?cap_first}IP(std::string ${subcomponent.getName()}IP);
</#list>