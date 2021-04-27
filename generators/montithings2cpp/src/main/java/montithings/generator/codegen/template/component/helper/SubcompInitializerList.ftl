<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">


<#list comp.subComponents as subcomponent>
    ${subcomponent.getName()}( instanceName + ".${subcomponent.getName()}",
    startDelays
    <#if config.getSplittingMode().toString() == "OFF">
        <#list ComponentHelper.getParamValues(subcomponent) as param >
            <#if param?index==0>,</#if>
            ${param}<#sep>,</#sep>
        </#list>
    </#if>)<#sep>,</#sep>
</#list>