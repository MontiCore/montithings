<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}

<#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
  std::unique_ptr${"<DDSRecorder>"} ddsRecorder;
  int recorderMessageId = 1;
</#if>
