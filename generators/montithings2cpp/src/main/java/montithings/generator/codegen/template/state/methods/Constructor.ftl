<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${className} (${Utils.printConfigurationParametersAsList(comp)})
<#if comp.getParameters()?has_content>:</#if>
<#list comp.getParameters() as param >
    ${param.getName()} (${param.getName()})
    <#sep>,</#sep>
</#list>
{}