<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}
<#include "/template/ConfigPreamble.ftl">

<#if recordingEnabled && portSymbol.isIncoming()>
  ddsRecorder = std::make_unique${"<DDSRecorder>"}();
  ddsRecorder->setInstanceName(instanceName);
  ddsRecorder->setTopicName(instanceName + ".${portSymbol.getName()}/out");
  ddsRecorder->setPortName("${portSymbol.getName()}");
  ddsRecorder->initParticipant(argc, argv);
  ddsRecorder->init();
</#if>