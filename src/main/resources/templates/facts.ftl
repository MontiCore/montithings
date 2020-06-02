<#list ast.properties as device, props>
    <#list props as key, value>
property(${key}, ${value}, ${device}).
    </#list>
</#list>