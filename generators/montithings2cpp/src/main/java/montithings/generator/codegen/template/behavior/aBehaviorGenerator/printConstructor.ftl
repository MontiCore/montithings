${tc.signature("comp","compname")}
${compname}Impl(${Utils.printConfigurationParametersAsList(comp)})
<#if !(comp.getParameters()?size == 0)>
    :
</#if>
<#list comp.getParameters() as param >
    ${param.getName()} (${param.getName()})<#sep>,</#sep>
</#list>
{
}