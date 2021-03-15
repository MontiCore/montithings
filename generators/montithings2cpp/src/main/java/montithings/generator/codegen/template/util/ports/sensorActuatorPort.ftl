<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol", "portTemeplateName", "existsHWC")}
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
#pragma once
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"

<#if config.getRecordingMode().toString() == "ON">
  #include ${"<dds/recorder/DDSRecorder.h>"}
  #include "dds/recorder/MessageWithClockContainer.h"
</#if>

${defineHookPoint("<CppBlock>?portTemplate:include")}
template${r"<class T>"}
class ${Names.getSimpleName(portTemeplateName)?cap_first}<#if existsHWC>TOP</#if> : public Port${r"<T>"}{
<#if config.getRecordingMode().toString() == "ON">
private:
  std::unique_ptr${"<DDSRecorder>"} ddsRecorder;
  int recorderMessageId = 1;
</#if>

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
    <#if config.getRecordingMode().toString() == "ON">
        recordMessage(nextVal);
    </#if>

    Port${"<T>"}::setNextValue(nextVal);
  }

  <#if config.getRecordingMode().toString() == "ON">
    void recordMessage(T value) {
      DDSMessage::Message message;
      message.id = recorderMessageId;

      MessageWithClockContainer ${"<T>"} container;
      container.message = value;
      container.vectorClock = ddsRecorder->getVectorClock();
      auto dataString = dataToJson(container);
      message.content = dataString.c_str();
      ddsRecorder->recordMessage(message, (char*) "${portSymbol.getFullName()}/out",
      ddsRecorder->getVectorClock());

      recorderMessageId++;
    }
  </#if>

  ${Names.getSimpleName(portTemeplateName)?cap_first} (){
    <#if config.getRecordingMode().toString() == "ON">
      ddsRecorder = std::make_unique${"<DDSRecorder>"}();
      ddsRecorder->setInstanceName("${portSymbol.getFullName()}");
      ddsRecorder->setPortIdentifier("${portSymbol.getFullName()}/out");
      ddsRecorder->init();
    </#if>
  }
};