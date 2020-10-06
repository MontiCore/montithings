<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign ConfigParams = tc.instantiate("montithings.generator.codegen.ConfigParams")>

    <#if (comp.isAtomic()) >
        ${tc.includeArgs("template.util.printSetupAtomic", [comp, compname])}
    <#else>
        ${tc.includeArgs("template.util.printSetupComposed", [comp, compname, config])}
    </#if>