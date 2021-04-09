<#include "/template/Preamble.ftl">
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = compname + "Manager">
<#if existsHWC>
    <#assign className += "TOP">
</#if>