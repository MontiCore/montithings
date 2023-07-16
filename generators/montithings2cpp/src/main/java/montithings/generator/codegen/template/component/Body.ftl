<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if comp.isDecomposed()>
  <#if !(splittingModeDisabled) && brokerDisabled>
    ${tc.includeArgs("template.component.helper.SubcompMethodDefinitions", [comp, config])}
  </#if>

  <#if needsRunMethod>
    ${tc.includeArgs("template.component.methods.Run", [comp, config, className])}
  </#if>
  ${tc.includeArgs("template.component.methods.ComputeDecomposed", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.StartDecomposed", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.StopDecomposed", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.SetupComposed", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.InitComposed", [comp, config, className])}
  <#if brokerIsMQTT>
    ${tc.includeArgs("template.component.methods.SendKeepAlive", [comp, config, className])}
    ${tc.includeArgs("template.component.methods.SendConnectionString", [comp, config, className])}
  </#if>

  <#if splittingModeDisabled>
    <#list comp.getSubComponents() as subcomponent>
      ${tc.includeArgs("template.component.methods.GetSubcomp", [comp, className, subcomponent, config])}
    </#list>
  </#if>
<#else>
  ${tc.includeArgs("template.component.methods.ComputeAtomic", [comp, config, className, ""])}
  <#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
    <#assign everyBlockName = ComponentHelper.getEveryBlockName(comp, everyBlock)>
    ${tc.includeArgs("template.component.methods.ComputeAtomic", [comp, config, className, everyBlockName])}
  </#list>
  ${tc.includeArgs("template.component.methods.StartAtomic", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.StopAtomic", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.Run", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.SetupAtomic", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.InitAtomic", [comp, config, className])}
  <#if brokerIsMQTT>
    ${tc.includeArgs("template.component.methods.SendKeepAlive", [comp, config, className])}
    ${tc.includeArgs("template.component.methods.SendConnectionString", [comp, config, className])}
  </#if>  ${tc.includeArgs("template.component.methods.SetResult", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.RunEveryBlocks", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.GetImpl", [comp, className])}
</#if>
<#if ComponentHelper.isDSLComponent(comp,config)>
  ${tc.includeArgs("template.component.methods.PythonReceiver",[comp, config, className])}
</#if>

<#if ComponentHelper.componentHasPorts(comp)>
  ${tc.includeArgs("template.interface.hooks.MethodDefinition", [comp, className])}
</#if>

${tc.includeArgs("template.logtracing.hooks.GetterDefinition", [comp, config, className])}
${tc.includeArgs("template.logtracing.hooks.InitLogTracerDefinition", [comp, config])}

${tc.includeArgs("template.component.methods.ShouldCompute", [comp, config, className])}

<#list ComponentHelper.getPortSpecificMTBehaviors(comp) as behavior>
${tc.includeArgs("template.component.methods.ShouldComputePortSpecificBehavior", [behavior, comp, config, className])}
</#list>

<#if ComponentHelper.retainState(comp)>
  ${tc.includeArgs("template.component.methods.RestoreState", [comp, config, className])}
</#if>

${tc.includeArgs("template.component.methods.GetState", [comp, className])}

<#if brokerIsMQTT>
  ${tc.includeArgs("template.component.methods.PublishConfigForSubcomponent", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.PublishConnectors", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.OnMessage", [comp, config, className])}
  ${tc.includeArgs("template.component.methods.GetMqttClientInstance", [comp, config, className])}
  <#if ComponentHelper.shouldGenerateCompatibilityHeartbeat(comp)>
    <#if ComponentHelper.getPortsWithTestBlocks(comp)?size <= 0>
        ${tc.includeArgs("template.component.methods.GetMqttClientSenderInstance", [comp, config, className, ""])}
        ${tc.includeArgs("template.component.methods.GetSubscriptionsToSend", [comp, config, className, ""])}
    <#else>
      <#list ComponentHelper.getPortsWithTestBlocks(comp) as p>
        ${tc.includeArgs("template.component.methods.GetMqttClientSenderInstance", [comp, config, className, p.getName()])}
        ${tc.includeArgs("template.component.methods.GetSubscriptionsToSend", [comp, config, className, p.getName()])}
        ${tc.includeArgs("template.component.methods.SetIsConnected", [comp, config, className, p.getName()])}
      </#list>
    </#if>
    ${tc.includeArgs("template.component.methods.SendCompatibilityHeartbeat", [comp, config, className])}
  </#if>
</#if>

<#if brokerIsDDS>
  ${tc.includeArgs("template.component.methods.SetDDSCmdArgs", [comp, config, className])}
</#if>

${tc.includeArgs("template.component.methods.OnEvent", [comp, config, className])}
${tc.includeArgs("template.component.methods.ThreadJoin", [comp, config, className])}
${tc.includeArgs("template.component.methods.CheckPostconditions", [comp, config, className])}

<#if comp.getPorts()?size gt 0>
  ${tc.includeArgs("template.component.methods.GetConnectionString", [comp, config, className, comp.getName(), comp.getPorts()])}
  <#list ComponentHelper.getInterfaceClassNames(comp, false) as interface>
    ${tc.includeArgs("template.component.methods.GetConnectionString", [comp, config, className, interface, ComponentHelper.getPortsOfInterface(interface, comp)])}
  </#list>
</#if>

${tc.includeArgs("template.component.methods.Constructor", [comp, config, className])}