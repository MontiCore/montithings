<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign GeneratorHelper = tc.instantiate("montithings.generator.helper.GeneratorHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>

${tc.includeArgs("template.util.ports.printMethodBodies", [comp.getPorts(), comp, compname, config, className])}

<#if comp.isDecomposed()>
    <#if config.getSplittingMode().toString() != "OFF">
        ${tc.includeArgs("template.util.subcomponents.printMethodDefinitions", [comp, config])}
    </#if>

    <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config)>
        ${tc.includeArgs("template.componentGenerator.printRun", [comp, compname, className])}
    </#if>
    ${tc.includeArgs("template.componentGenerator.printComputeDecomposed", [comp, compname, config, className])}
    ${tc.includeArgs("template.componentGenerator.printStartDecomposed", [comp, compname, config, className])}
<#else>
    ${tc.includeArgs("template.componentGenerator.printComputeAtomic", [comp, compname, className])}
    ${tc.includeArgs("template.componentGenerator.printStartAtomic", [comp, compname, className])}
    ${tc.includeArgs("template.componentGenerator.printRun", [comp, compname, className])}
    ${tc.includeArgs("template.componentGenerator.printInitialize", [comp, compname, config, className])}
    ${tc.includeArgs("template.componentGenerator.printSetResult", [comp, compname, config, className])}
</#if>

${tc.includeArgs("template.componentGenerator.printShouldComputeCheck", [comp, compname, className])}

${tc.includeArgs("template.util.setup.Setup", [comp, compname, config, className])}
<#if ComponentHelper.retainState(comp)>
  ${tc.includeArgs("template.componentGenerator.printRestoreState", [comp, config, className])}
</#if>
${tc.includeArgs("template.util.init.Init", [comp, compname, config, className])}

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.componentGenerator.printPublishConfigForSubcomponent", [comp, config, className])}
  ${tc.includeArgs("template.componentGenerator.printPublishConnectors", [comp, config, className])}
  ${tc.includeArgs("template.componentGenerator.printOnMessage", [comp, config, className])}
</#if>

${tc.includeArgs("template.componentGenerator.printOnEvent", [comp, config, className])}
${tc.includeArgs("template.componentGenerator.printThreadJoin", [comp, config, className])}

${tc.includeArgs("template.componentGenerator.printConstructor", [comp, compname, config, className])}