<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::start(){
<#if needsRunMethod>
    threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::run, this});
<#else>
    <#if splittingModeDisabled || ComponentHelper.shouldIncludeSubcomponents(comp, config)>
        <#list comp.subComponents as subcomponent >
            this->${subcomponent.getName()}.start();
        </#list>
    </#if>
</#if>
<#if ComponentHelper.isDSLComponent(comp,config)>
threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::python_start, this});
</#if>
}