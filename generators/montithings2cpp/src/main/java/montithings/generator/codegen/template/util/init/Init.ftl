<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config", "className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if (comp.isAtomic()) >
    ${tc.includeArgs("template.util.init.printInitAtomic", [comp, compname, className])}
<#else>
    ${tc.includeArgs("template.util.init.printInitComposed", [comp, compname, config, className])}
</#if>