<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol")}
<#include "/template/ConfigPreamble.ftl">

  <#if recordingEnabled && portSymbol.isIncoming()>
    void recordMessage(T value) {

      DDSMessage::Message message;
      message.id = recorderMessageId;
      MessageWithClockContainer ${"<T>"} container;
      container.message = value;

      VectorClock::updateVectorClock(VectorClock::getVectorClock(), instanceName);
      container.vectorClock = VectorClock::getVectorClock();
      auto dataString = dataToJson(container.message);
      message.content = dataString.c_str();
      ddsRecorder->recordMessage(message, (instanceName + ".${portSymbol.getName()}/out").c_str(), VectorClock::getVectorClock(), true);

      recorderMessageId++;
    }
  </#if>