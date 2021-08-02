<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "isSensor", "portTemplateName", "everyTagOpt")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
<#include "/template/Copyright.ftl">
#pragma once
#include "easyloggingpp/easylogging++.h"
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"
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

  void sendToExternal(tl::optional${r"<T>"} nextVal) override
  {
  ${defineHookPoint("<CppBlock>?portTemplate:consume")}
  }

  void setNextValue(T nextVal) override {
    <#if config.getRecordingMode().toString() == "ON" && isSensor>
        recordMessage(nextVal);
    </#if>

    Port${"<T>"}::setNextValue(nextVal);
  }


  <#if config.getMessageBroker().toString() == "DDS">
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