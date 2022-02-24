<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}

<#if recordingEnabled && portSymbol.isIncoming()>
  std::unique_ptr${"<DDSRecorder>"} ddsRecorder;
  int recorderMessageId = 1;
</#if>
