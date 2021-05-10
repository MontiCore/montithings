<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if comp.isDecomposed()>
  <#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
    ${tc.includeArgs("template.component.helper.SubcompMethodDefinitions", [comp, config])}
  </#if>

  <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config)>
    ${tc.includeArgs("template.component.methods.Run", [comp, config, className])}
  </#if>
  ${tc.includeArgs("template.component.methods.ComputeDecomposed", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.StartDecomposed", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.SetupComposed", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.InitComposed", [comp, config, className])}
<#else>
  ${tc.includeArgs("template.component.methods.ComputeAtomic", [comp, config, className, ""])}
  <#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
    <#assign everyBlockName = ComponentHelper.getEveryBlockName(comp, everyBlock)>
    ${tc.includeArgs("template.component.methods.ComputeAtomic", [comp, config, className, everyBlockName])}
  </#list>
  ${tc.includeArgs("template.component.methods.StartAtomic", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.Run", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.SetupAtomic", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.InitAtomic", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.Initialize", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.SetResult", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.RunEveryBlocks", [comp, config, className])}
</#if>

<#if !(comp.getPorts()?size == 0)>
  ${tc.includeArgs("template.interface.hooks.MethodDefinition", [comp, className])}
</#if>

${tc.includeArgs("template.logtracing.hooks.GetterDefinition", [comp, config, className])}

${tc.includeArgs("template.component.methods.ShouldCompute", [comp, config, className])}

<#list ComponentHelper.getPortSpecificBehaviors(comp) as behavior>
${tc.includeArgs("template.component.methods.ShouldComputePortSpecificBehavior", [behavior, comp, config, className])}
</#list>

<#if ComponentHelper.retainState(comp)>
  ${tc.includeArgs("template.component.methods.RestoreState", [comp, config, className])}
</#if>

${tc.includeArgs("template.component.methods.printGetState", [comp, className])}

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.component.methods.PublishConfigForSubcomponent", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.PublishConnectors", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.OnMessage", [comp, config, className])}
</#if>

<#if config.getMessageBroker().toString() == "DDS">
  ${tc.includeArgs("template.component.methods.SetDDSCmdArgs", [comp, config, className])}
</#if>

${tc.includeArgs("template.component.methods.OnEvent", [comp, config, className])}
${tc.includeArgs("template.component.methods.ThreadJoin", [comp, config, className])}
${tc.includeArgs("template.component.methods.CheckPostconditions", [comp, config, className])}

${tc.includeArgs("template.component.methods.Constructor", [comp, config, className])}