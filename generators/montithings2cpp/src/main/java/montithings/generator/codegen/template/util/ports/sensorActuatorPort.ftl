<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "portSymbol", "portTemeplateName", "everyTagOpt" "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
<#include "/template/Copyright.ftl">
#pragma once
#include "easyloggingpp/easylogging++.h"
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"
#include ${"<thread>"}

${tc.includeArgs("template.util.ports.helper.DDSRecorderIncludes", [config, portSymbol])}

${defineHookPoint("<CppBlock>?portTemplate:include")}
template${r"<class T>"}
class ${Names.getSimpleName(portTemeplateName)?cap_first}<#if existsHWC>TOP</#if> : public Port${r"<T>"}{
protected:

  ${tc.includeArgs("template.util.ports.helper.DDSRecorderDeclarations", [config, portSymbol])}
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
    <#if config.getRecordingMode().toString() == "ON" && portSymbol.isIncoming()>
        recordMessage(nextVal);
    </#if>

    Port${"<T>"}::setNextValue(nextVal);
  }

  ${tc.includeArgs("template.util.ports.methods.DDSRecorderRecord", [config, portSymbol])}

  <#if config.getMessageBroker().toString() == "DDS">
    ${Names.getSimpleName(portTemeplateName)?cap_first} (std::string instanceName, int argc, char *argv[]) : instanceName(instanceName)
  <#else>
    ${Names.getSimpleName(portTemeplateName)?cap_first} (std::string instanceName) : instanceName(instanceName)
  </#if>
  {
    ${tc.includeArgs("template.util.ports.helper.DDSRecorderInit", [config, portSymbol])}

    ${defineHookPoint("<CppBlock>?portTemplate:init")}
    <#if everyTagOpt.isPresent()>
      loopThread = std::thread( [this] { loop (); } );
    </#if>
  }
};