<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/CompPreamble.ftl">
<#include "/template/TcPreamble.ftl">
<#assign generics = Utils.printFormalTypeParameters(comp)>
<#assign className = compname + "Interface">
<#if existsHWC>
    <#assign className += "TOP">
</#if>