<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}

<#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
  ddsRecorder = std::make_unique${"<DDSRecorder>"}();
  ddsRecorder->setInstanceName(instanceName);
  ddsRecorder->setTopicName(instanceName + ".${portSymbol.getName()}/out");
  ddsRecorder->setPortName("${portSymbol.getName()}");
  ddsRecorder->initParticipant(argc, argv);
  ddsRecorder->init();
</#if>