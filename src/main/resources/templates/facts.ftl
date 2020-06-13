<#list ast.properties as device, props>
    <#list props as key, values>
        <#list values as value>
property(${key}, ${value}, ${device}).
        </#list>
    </#list>
</#list>