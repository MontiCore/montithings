<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "className")}
<#include "/template/Preamble.ftl">
<#include "/template/state/helper/GeneralPreamble.ftl">

<#list ComponentHelper.getVariablesAndParameters(comp) as var>
  ${tc.includeArgs("template.state.methods.GetVariable", [var, comp, config, existsHWC])}
  ${tc.includeArgs("template.state.methods.SetVariable", [var, comp, config, existsHWC])}
  ${tc.includeArgs("template.state.methods.PreSetVariable", [var, comp, config, existsHWC])}
  ${tc.includeArgs("template.state.methods.PostSetVariable", [var, comp, config, existsHWC])}
</#list>

<#list ComponentHelper.getArcFieldVariables(comp) as var>
  <#if ComponentHelper.hasAgoQualification(comp, var)>
    ${tc.includeArgs("template.state.methods.AgoGetVariable", [var, comp, config, existsHWC])}
    ${tc.includeArgs("template.state.methods.CleanDequeOfVariable", [var, comp, config, existsHWC])}
  </#if>
</#list>

${tc.includeArgs("template.state.methods.SetInstanceName", [comp, config, existsHWC])}

${tc.includeArgs("template.util.statechart.methods.Get", [comp, config, className])}
${tc.includeArgs("template.util.statechart.methods.Set", [comp, config, className])}

${tc.includeArgs("template.state.methods.restore.Setup", [comp, className])}
${tc.includeArgs("template.state.methods.restore.SerializeState", [comp, className])}
${tc.includeArgs("template.state.methods.restore.StoreState", [comp, className])}
${tc.includeArgs("template.state.methods.restore.RestoreState", [comp, className])}
${tc.includeArgs("template.state.methods.restore.RestoreState2", [comp, className])}

${tc.includeArgs("template.state.methods.restore.IsReplayFinished", [comp, className])}
${tc.includeArgs("template.state.methods.restore.IsReplayTimeout", [comp, className])}
${tc.includeArgs("template.state.methods.restore.IsReceivedState", [comp, className])}
${tc.includeArgs("template.state.methods.restore.IsRestoredState", [comp, className])}

<#if brokerIsMQTT>
  ${tc.includeArgs("template.state.methods.restore.RequestState", [comp, className])}
  ${tc.includeArgs("template.state.methods.restore.RequestReplay", [comp, className])}
  ${tc.includeArgs("template.state.methods.restore.PublishState", [comp, className])}
  ${tc.includeArgs("template.state.methods.restore.OnMessage", [comp, className])}
</#if>