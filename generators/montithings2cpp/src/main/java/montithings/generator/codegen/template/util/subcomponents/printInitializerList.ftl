${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#list comp.subComponents as subcomponent>
    ${subcomponent.getName()}( "${subcomponent.getName()}"
    <#if config.getSplittingMode().toString() == "OFF">
        <#list ComponentHelper.getParamValues(subcomponent) as param >
            <#sep>,</#sep>
                param
        </#list>
    </#if>)<#sep>,</#sep>
</#list>