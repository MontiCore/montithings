<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("path","portTmeplateName")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:include", path+"."+portTmeplateName+"Include")}
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:body", path+"."+portTmeplateName+"Body")}
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:getExternalMessages", path+"."+portTmeplateName+"GetExternalMessages")}
${glex.bindTemplateHookPoint("<CppBlock>?portTemplate:sendToExternal", path+"."+portTmeplateName+"SendToExternal")}
#pragma once
#include "tl/optional.hpp"
#include "Port.h"
#include "Utils.h"
${defineHookPoint("<CppBlock>?portTemplate:include")}
template${r"<class T>"}
class ${portTmeplateName?cap_first} : public Port${r"<T>"}{
    ${defineHookPoint("<CppBlock>?portTemplate:body")}
    public: void getExternalMessages() override
    {
        ${defineHookPoint("<CppBlock>?portTemplate:getExternalMessages")}
    }
    void sendToExternal(tl::optional${r"<T>"} nextVal) override
    {
        ${defineHookPoint("<CppBlock>?portTemplate:sendToExternal")}
    }
    ${portTmeplateName?cap_first} (){}
};