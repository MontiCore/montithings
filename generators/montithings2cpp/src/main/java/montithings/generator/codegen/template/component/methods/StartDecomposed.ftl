<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::start(){
<#if dummyName2>
    threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::run, this});
<#else>
    <#if dummyName8>
        <#list comp.subComponents as subcomponent >
            this->${subcomponent.getName()}.start();
        </#list>
    </#if>
</#if>
}