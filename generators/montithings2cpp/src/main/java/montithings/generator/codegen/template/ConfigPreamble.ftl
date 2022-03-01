<#-- (c) https://github.com/MontiCore/monticore -->
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
