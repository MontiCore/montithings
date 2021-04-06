<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol", "portTemeplateName", "existsHWC")}
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
#pragma once
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"

<#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
  #include ${"<dds/recorder/DDSRecorder.h>"}
  #include "dds/recorder/MessageWithClockContainer.h"
  #include "dds/recorder/VectorClock.h"
</#if>

${defineHookPoint("<CppBlock>?portTemplate:include")}
template${r"<class T>"}
class ${Names.getSimpleName(portTemeplateName)?cap_first}<#if existsHWC>TOP</#if> : public Port${r"<T>"}{
<#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
private:
  std::unique_ptr${"<DDSRecorder>"} ddsRecorder;
  int recorderMessageId = 1;
</#if>
  std::string instanceName;

  ${defineHookPoint("<CppBlock>?portTemplate:body")}
  public:
  bool
  hasValue (sole::uuid requester) override
  {
  return true;
  }

  void getExternalMessages() override
  {
  ${defineHookPoint("<CppBlock>?portTemplate:provide")}
  }

  void sendToExternal(tl::optional${r"<T>"} nextVal) override
  {
  ${defineHookPoint("<CppBlock>?portTemplate:consume")}
  }

  void setNextValue(T nextVal) override {
    <#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
        recordMessage(nextVal);
    </#if>

    Port${"<T>"}::setNextValue(nextVal);
  }

  <#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
    void recordMessage(T value) {
      DDSMessage::Message message;
      message.id = recorderMessageId;

      MessageWithClockContainer ${"<T>"} container;
      container.message = value;
      container.vectorClock = VectorClock::getVectorClock();
      auto dataString = dataToJson(container.message);
      message.content = dataString.c_str();
      ddsRecorder->recordMessage(message, (instanceName + ".${portSymbol.getName()}/out").c_str(),
        VectorClock::getVectorClock(), true);

      recorderMessageId++;
    }
  </#if>

  ${Names.getSimpleName(portTemeplateName)?cap_first} (std::string instanceName) : instanceName(instanceName) {
    <#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
      ddsRecorder = std::make_unique${"<DDSRecorder>"}();
      ddsRecorder->setInstanceName(instanceName);
      ddsRecorder->setTopicName(instanceName + ".${portSymbol.getName()}/out");
      ddsRecorder->setPortName("${portSymbol.getName()}");
      ddsRecorder->init();
    </#if>
  }
};