<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
  ddsParticipant.setComp(&cmp);

  ddsParticipant.initializeOutgoingPorts();
  ddsParticipant.initializeConnectorConfigPorts();
  ddsParticipant.publishConnectorConfig();
</#if>