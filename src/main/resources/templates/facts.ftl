<#list ast.properties as device, props>
property("device", 1, "${device}").
    <#list props as key, values>
        <#list values as value>
property("${key}", "${value}", "${device}").
        </#list>
    </#list>
</#list>