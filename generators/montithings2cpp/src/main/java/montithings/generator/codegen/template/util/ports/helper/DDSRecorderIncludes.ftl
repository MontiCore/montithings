<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("config", "portSymbol")}

<#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
  #include ${"<dds/recorder/DDSRecorder.h>"}
  #include "dds/recorder/MessageWithClockContainer.h"
  #include "dds/recorder/VectorClock.h"
</#if>
