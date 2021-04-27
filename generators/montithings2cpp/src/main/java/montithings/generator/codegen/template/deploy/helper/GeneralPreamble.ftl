<#include "/template/Preamble.ftl">
<#assign className = "Deploy" + compname>
<#if existsHWC>
    <#assign className += "TOP">
</#if>