<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}
<#include "/template/Preamble.ftl">

<#if recordingEnabled && portSymbol.isIncoming()>
  std::unique_ptr${"<DDSRecorder>"} ddsRecorder;
  int recorderMessageId = 1;
</#if>
