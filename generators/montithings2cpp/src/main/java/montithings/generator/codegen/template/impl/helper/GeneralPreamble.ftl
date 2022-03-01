<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/Preamble.ftl">
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = compname + "Impl">
<#if existsHWC>
    <#assign className += "TOP">
</#if>