<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}
<#include "/template/ConfigPreamble.ftl">

<#if recordingEnabled && portSymbol.isIncoming()>
  #include ${"<record-and-replay/recorder/DDSRecorder.h>"}
  #include "record-and-replay/recorder/MessageWithClockContainer.h"
  #include "record-and-replay/recorder/VectorClock.h"
</#if>
