<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/CompPreamble.ftl">
<#assign className = compname + "LogTraceObserver">
<#if existsHWC>
    <#assign className += "TOP">
</#if>