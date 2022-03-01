<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/CompPreamble.ftl">
<#include "/template/TcPreamble.ftl">
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = compname + "Input">
<#if existsHWC>
    <#assign className += "TOP">
</#if>