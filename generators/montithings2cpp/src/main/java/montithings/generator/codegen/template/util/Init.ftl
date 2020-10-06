<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

    <#if (comp.isAtomic()) >
        ${tc.includeArgs("template.util.printInitAtomic", [comp, compname])}
    <#else>
        ${tc.includeArgs("template.util.printInitComposed", [comp, compname, config])}
    </#if>