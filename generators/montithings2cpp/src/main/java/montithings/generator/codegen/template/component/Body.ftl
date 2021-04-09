<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if comp.isDecomposed()>
  <#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
    ${tc.includeArgs("template.util.subcomponents.printMethodDefinitions", [comp, config])}
  </#if>

  <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config)>
    ${tc.includeArgs("template.component.methods.Run", [comp, compname, className])}
  </#if>
  ${tc.includeArgs("template.component.methods.ComputeDecomposed", [comp, compname, config, className])}
  ${tc.includeArgs("template.component.methods.StartDecomposed", [comp, compname, config, className])}
<#else>
  ${tc.includeArgs("template.component.methods.ComputeAtomic", [comp, compname, className, ""])}
  <#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
    <#assign everyBlockName = ComponentHelper.getEveryBlockName(comp, everyBlock)>
    ${tc.includeArgs("template.component.methods.ComputeAtomic", [comp, compname, className, everyBlockName])}
  </#list>
  ${tc.includeArgs("template.component.methods.StartAtomic", [comp, compname, className])}
  ${tc.includeArgs("template.component.methods.Run", [comp, compname, className])}
  ${tc.includeArgs("template.component.methods.Initialize", [comp, compname, config, className])}
  ${tc.includeArgs("template.component.methods.SetResult", [comp, compname, config, className])}
  ${tc.includeArgs("template.component.methods.RunEveryBlocks", [comp, compname, className])}
</#if>

<#if !(comp.getPorts()?size == 0)>
  ${tc.includeArgs("template.interface.hooks.MethodDefinition", [comp, className])}
</#if>
${tc.includeArgs("template.component.methods.ShouldCompute", [comp, compname, className])}

${tc.includeArgs("template.util.setup.Setup", [comp, compname, config, className])}
<#if ComponentHelper.retainState(comp)>
  ${tc.includeArgs("template.component.methods.RestoreState", [comp, config, className])}
</#if>
${tc.includeArgs("template.util.init.Init", [comp, compname, config, className])}

<#if config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.component.methods.PublishConfigForSubcomponent", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.PublishConnectors", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.OnMessage", [comp, config, className])}
</#if>

${tc.includeArgs("template.component.methods.OnEvent", [comp, config, className])}
${tc.includeArgs("template.component.methods.ThreadJoin", [comp, config, className])}

${tc.includeArgs("template.component.methods.Constructor", [comp, compname, config, className])}