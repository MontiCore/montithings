${tc.signature("comp","isTOP")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${comp.getName()}Impl<#if isTOP>TOP</#if>(${Utils.printConfigurationParametersAsList(comp)})
<#if comp.getParameters()?has_content>
    :
    <#list comp.getParameters() as param >
        ${param.getName()} (${param.getName()})<#sep>,</#sep>
    </#list>
    {
    }
<#else>
    = default;
</#if>