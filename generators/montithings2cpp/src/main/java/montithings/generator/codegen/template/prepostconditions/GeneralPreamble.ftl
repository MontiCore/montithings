<#assign prefix = "">
<#if isPrecondition>
    <#assign prefix = "Pre">
<#else>
    <#assign prefix = "Post">
</#if>
<#assign className = compname + prefix + "condition">
<#if existsHWC>
    <#assign className += "TOP">
</#if>