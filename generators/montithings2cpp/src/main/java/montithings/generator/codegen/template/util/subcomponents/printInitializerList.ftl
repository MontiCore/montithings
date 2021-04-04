<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#list comp.subComponents as subcomponent>
    ${subcomponent.getName()}( "${subcomponent.getName()}"
    <#if config.getSplittingMode().toString() == "OFF" || ComponentHelper.shouldIncludeSubcomponents(comp, config)>
        <#list ComponentHelper.getParamValues(subcomponent) as param >
            <#if param?index==0>,</#if>
            ${param}<#sep>,</#sep>
        </#list>
    </#if>)<#sep>,</#sep>
</#list>