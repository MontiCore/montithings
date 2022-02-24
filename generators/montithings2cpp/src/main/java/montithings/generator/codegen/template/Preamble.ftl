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
<#assign compname = comp.getName()>
<#assign generics = Utils.printFormalTypeParameters(comp)>

<#assign brokerIsMQTT = config.getMessageBroker().toString() == "MQTT">
<#assign brokerDisabled = config.getMessageBroker().toString() == "OFF">
<#assign brokerIsDDS = config.getMessageBroker().toString() == "DDS">
<#assign needsDDS = (!splittingModeDisabled && !targetPlatformIsDsaVcg && !targetPlatformIsDsaLab && brokerIsDDS)> <#-- todo richtiger Name? -->

<#assign replayEnabled = config.getReplayMode().toString() == "ON">
<#assign recordingEnabled = config.getRecordingMode().toString() == "ON">
<#assign logTracingEnabled = config.getLogTracing().toString() == "ON">

<#assign hasIncomingPorts = comp.getAllIncomingPorts()?has_content> <#-- todo warum verschiedene implementierungen für check incoming ports? -->
<#assign hasNoIncomingPorts = comp.getAllIncomingPorts()?size == 0>
<#assign hasNoPorts = comp.getPorts()?size == 0>

<#assign splittingModeIsDistributed = config.getSplittingMode().toString() == "DISTRIBUTED">
<#assign splittingModeIsLocal = config.getSplittingMode().toString() == "LOCAL">
<#assign splittingModeDisabled = config.getSplittingMode().toString() == "OFF">


<#assign targetPlatformIsRaspberry = config.getTargetPlatform().toString() == "RASPBERRY">
<#assign targetPlatformIsDsaVcg = config.getTargetPlatform().toString() == "DSA_VCG">
<#assign targetPlatformIsDsaLab = config.getTargetPlatform().toString() == "DSA_LAB">

<#assign dummyName1 = (!target.isPresentComponent() && subcomponent.getName() == connector.getSource().getComponent())>
<#assign dummyName2 = (ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config))>
<#assign dummyName3 = (comp.isAtomic() || ComponentHelper.getPortSpecificBehaviors(comp)?size gt 0)>
<#assign dummyName4 = (Utils.getGenericParameters(comp)?seq_contains(subcomponent.getGenericType().getName()))>
<#assign dummyName5 = (GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent())>
<#assign dummyName6 = (behavior == "false" || ComponentHelper.usesPort(behavior, inPort))>
<#assign dummyName7 = (config.getTemplatedPorts()?seq_contains(port) && additionalPort!="Optional.empty")>
<#assign dummyName8 = (splittingModeDisabled || ComponentHelper.shouldIncludeSubcomponents(comp, config))>
<#assign dummyName9 = (replayEnabled && !ComponentHelper.isFlaggedAsGenerated(comp))>
<#assign dummyName10 = (brokerDisabled || ComponentHelper.shouldIncludeSubcomponents(comp,config))>
<#assign dummyName11 = (comp.getAllIncomingPorts()?size gt 0 && !ComponentHelper.hasSyncGroups(comp))>
<#assign dummyname12 = (ComponentHelper.isTimesync(comp) || (!splittingModeDisabled && brokerDisabled))>
<#assign dummyName13 = (comp.getParameters()?size gt 0 || ComponentHelper.getSIUnitPortNames(comp)?size gt 0 || config.getTypeArguments(comp)?size gt 0)>
<#assign dummyName14 = (!ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.getName()))>
<#assign dummyName15 = ComponentHelper.getPortsInGuardExpression(statement.guard)?size == 0>
<#assign dummyName16 = (!connector.getSource().isPresentComponent() && subcomponent.getName() == target.getComponent())>
<#assign dummyName17 = (target.isPresentComponent() && subcomponent.getName() == target.getComponent())>