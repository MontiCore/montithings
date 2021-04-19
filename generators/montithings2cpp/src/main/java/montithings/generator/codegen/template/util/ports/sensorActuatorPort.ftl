<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("portTemeplateName", "existsHWC")}
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
#pragma once
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"
${defineHookPoint("<CppBlock>?portTemplate:include")}
template${r"<class T>"}
class ${Names.getSimpleName(portTemeplateName)?cap_first}<#if existsHWC>TOP</#if> : public Port${r"<T>"}{
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

  ${Names.getSimpleName(portTemeplateName)?cap_first} () {
    ${defineHookPoint("<CppBlock>?portTemplate:init")}
  }
};