<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::start(){
<#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config)>
    threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::run, this});
<#else>
    <#if config.getSplittingMode().toString() == "OFF" || ComponentHelper.shouldIncludeSubcomponents(comp, config)>
        <#list comp.subComponents as subcomponent >
            this->${subcomponent.getName()}.start();
        </#list>
    </#if>
</#if>
}