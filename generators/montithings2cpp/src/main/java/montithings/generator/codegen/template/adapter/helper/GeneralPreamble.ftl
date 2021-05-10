<#-- (c) https://github.com/MontiCore/monticore -->
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign escape = Utils.escapePackage(packageName)>
<#assign className = compname + "Adapter">
<#if existsHWC>
    <#assign className += "TOP">
</#if>