<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${className}(${Utils.printConfigurationParametersAsList(comp)})
<#if comp.getParameters()?has_content>
    :
    <#list comp.getParameters() as param >
        ${param.getName()} (${param.getName()})<#sep>,</#sep>
    </#list>
    {
    }
<#else>
    = default;
</#if>