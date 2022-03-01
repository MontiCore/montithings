<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/Preamble.ftl">
<#assign generics = Utils.printFormalTypeParameters(comp)>
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = compname + "State">
<#if existsHWC>
    <#assign className += "TOP">
</#if>