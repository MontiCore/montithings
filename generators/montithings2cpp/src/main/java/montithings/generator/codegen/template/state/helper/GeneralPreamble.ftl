<#include "/template/Preamble.ftl">
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = compname + "State">
<#if existsHWC>
    <#assign className += "TOP">
</#if>