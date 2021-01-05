<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${compname}.h"
<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
  #include "${compname}Manager.h"
<#elseif config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
  #include "${compname}DDSParticipant.h"
</#if>
#include ${"<chrono>"}
#include ${"<thread>"}
<#if config.getMessageBroker().toString() == "MQTT">
#include "MqttConfigRequester.h"
</#if>

int main<#if existsHWC>TOP</#if>(int argc, char* argv[])
{
${tc.includeArgs("template.deploy.ParameterCheck", [comp, config])}
${tc.includeArgs("template.deploy.CommunicationManager", [comp, config])}
return 0;
}