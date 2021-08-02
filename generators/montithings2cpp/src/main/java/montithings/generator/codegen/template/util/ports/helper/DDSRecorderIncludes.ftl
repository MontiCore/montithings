<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}

<#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
  #include ${"<record-and-replay/recorder/DDSRecorder.h>"}
  #include "record-and-replay/recorder/MessageWithClockContainer.h"
  #include "record-and-replay/recorder/VectorClock.h"
</#if>
