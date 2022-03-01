<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "isSensor", "portTemplateName", "everyTagOpt")}
<#include "/template/TcPreamble.ftl">
<#include "/template/ConfigPreamble.ftl">
<#include "/template/Copyright.ftl">
#pragma once
#include "easyloggingpp/easylogging++.h"
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"
#include "Message.h"
#include ${"<thread>"}


${defineHookPoint("<CppBlock>?portTemplate:include")}
template${r"<class T>"}
class ${Names.getSimpleName(portTemplateName)?cap_first}Port : public Port${r"<T>"}{
protected:

  std::string instanceName;
  <#if everyTagOpt.isPresent()>
    std::thread loopThread;

    void loop() {
    while (true)
    {
    auto end = std::chrono::high_resolution_clock::now()
    + std::chrono::${ComponentHelper.printTime(everyTagOpt.get().getSIUnitLiteral())};

    this->getExternalMessages();

    do
    {
    std::this_thread::yield();
    std::this_thread::sleep_for(std::chrono::milliseconds(1));
    } while (std::chrono::high_resolution_clock::now()  < end);
    }

    }
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

  template <typename A>
    void handleNextVal(tl::optional<Message<A>> nextValMessage)
      {
      // unpacking Message type
      tl::optional<A> nextVal;
      if(nextValMessage.has_value()) {
        Message<A> msg = nextValMessage.value();
        nextVal = msg.getPayload();
      }
      ${defineHookPoint("<CppBlock>?portTemplate:consume")}
  }


  void sendToExternal(tl::optional${r"<T>"} nextVal) override
  {
  // Type T is a Message type, hand written code, however, expects a primitive type
  // hence, unpack nextVal first
  handleNextVal(nextVal);
  }


  void setNextValue(T nextVal) override {
    <#if recordingEnabled && isSensor>
        recordMessage(nextVal);
    </#if>

    Port${"<T>"}::setNextValue(nextVal);
  }

  template <typename A>
    void setNextValue(A nextVal) {
    // hand written code does not wrap values into the Message typ
    // hence, it has to be done here
    setNextValue(Message<A>(nextVal));
  }


    <#if brokerIsDDS>
    ${Names.getSimpleName(portTemplateName)?cap_first}Port (std::string instanceName, int argc, char *argv[]) : instanceName(instanceName)
  <#else>
    ${Names.getSimpleName(portTemplateName)?cap_first}Port (std::string instanceName) : instanceName(instanceName)
  </#if>
  {
    ${defineHookPoint("<CppBlock>?portTemplate:init")}
    <#if everyTagOpt.isPresent()>
      loopThread = std::thread( [this] { loop (); } );
    </#if>
  }
};