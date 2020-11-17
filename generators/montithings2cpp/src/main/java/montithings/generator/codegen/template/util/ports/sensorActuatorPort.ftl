<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("portTemeplateName")}
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:include", portTemeplateName+"Include")}
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:body", portTemeplateName+"Body")}
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:getExternalMessages", portTemeplateName+"GetExternalMessages")}
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:sendToExternal", portTemeplateName+"SendToExternal")}
#pragma once
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"
${defineHookPoint("<CppBlock>?portTemplate:include")}
template${r"<class T>"}
class ${Names.getSimpleName(portTemeplateName)?cap_first} : public Port${r"<T>"}{
    ${defineHookPoint("<CppBlock>?portTemplate:body")}
    public: void getExternalMessages() override
    {
        ${defineHookPoint("<CppBlock>?portTemplate:getExternalMessages")}
    }
    void sendToExternal(tl::optional${r"<T>"} nextVal) override
    {
        ${defineHookPoint("<CppBlock>?portTemplate:sendToExternal")}
    }
    ${Names.getSimpleName(portTemeplateName)?cap_first} (){}
};