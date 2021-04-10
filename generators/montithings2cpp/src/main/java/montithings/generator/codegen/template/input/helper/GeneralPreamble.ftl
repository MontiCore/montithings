<#include "/template/Preamble.ftl">
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = compname + "Input">
<#if existsHWC>
    <#assign className += "TOP">
</#if>