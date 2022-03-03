<#-- (c) https://github.com/MontiCore/monticore -->

<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign GeneratorHelper = tc.instantiate("montithings.generator.helper.GeneratorHelper")>
<#assign StatechartHelper = tc.instantiate("montithings.generator.helper.StatechartHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign PrettyPrinter = tc.instantiate("montithings._visitor.MontiThingsFullPrettyPrinter")>
<#assign TypesHelper = tc.instantiate("montithings.generator.helper.TypesHelper")>
<#assign TypesPrinter = tc.instantiate("montithings.generator.helper.TypesPrinter")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>

<#if config??>
    <#assign brokerIsMQTT = config.getMessageBroker().toString() == "MQTT">
    <#assign brokerDisabled = config.getMessageBroker().toString() == "OFF">
    <#assign brokerIsDDS = config.getMessageBroker().toString() == "DDS">

    <#assign replayEnabled = config.getReplayMode().toString() == "ON">
    <#assign recordingEnabled = config.getRecordingMode().toString() == "ON">
    <#assign logTracingEnabled = config.getLogTracing().toString() == "ON">

    <#assign splittingModeIsDistributed = config.getSplittingMode().toString() == "DISTRIBUTED">
    <#assign splittingModeIsLocal = config.getSplittingMode().toString() == "LOCAL">
    <#assign splittingModeDisabled = config.getSplittingMode().toString() == "OFF">

    <#assign targetPlatformIsRaspberry = config.getTargetPlatform().toString() == "RASPBERRY">
    <#assign targetPlatformIsDsaVcg = config.getTargetPlatform().toString() == "DSA_VCG">
    <#assign targetPlatformIsDsaLab = config.getTargetPlatform().toString() == "DSA_LAB">
    <#assign targetPlatformIsDsa = (targetPlatformIsDsaVcg || targetPlatformIsDsaLab)>

    <#assign needsDDS = (!(targetPlatformIsDsa) && !(splittingModeDisabled) && brokerIsDDS)>
    <#assign needsNng = (!(targetPlatformIsDsa) && !(splittingModeDisabled) && brokerDisabled)>
</#if>


<#if comp??>
    <#assign compname = comp.getName()>
    <#assign generics = Utils.printFormalTypeParameters(comp)>
</#if>

<#if comp?? && config??>
    <#assign needsRunMethod = (ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config))>
</#if>
