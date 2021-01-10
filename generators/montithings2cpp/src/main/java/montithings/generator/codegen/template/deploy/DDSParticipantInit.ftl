<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}DDSParticipant ddsParticipant(&cmp, argc, argv);
  ddsParticipant.initializePorts();
  ddsParticipant.publishConnectors();
</#if>