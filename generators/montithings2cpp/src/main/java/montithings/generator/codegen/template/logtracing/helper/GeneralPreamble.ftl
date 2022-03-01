<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/Preamble.ftl">
<#assign className = compname + "LogTraceObserver">
<#if existsHWC>
    <#assign className += "TOP">
</#if>