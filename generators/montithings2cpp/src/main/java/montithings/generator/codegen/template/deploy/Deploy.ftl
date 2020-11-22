<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${compname}.h"
<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
  #include "${compname}Manager.h"
<#elseif config.getMessageBroker().toString() == "DDS">
  #include "${compname}DDSParticipant.h"
</#if>
#include ${"<chrono>"}
#include ${"<thread>"}

int main(int argc, char* argv[])
{
${tc.includeArgs("template.deploy.ParameterCheck", [comp, config])}
${tc.includeArgs("template.deploy.CommunicationManager", [comp, config])}


cmp.setUp(<#if ComponentHelper.isTimesync(comp)>
  TIMESYNC
<#else>
  EVENTBASED
</#if>);
cmp.init();
<#if !ComponentHelper.isTimesync(comp)>
  cmp.start();
</#if>


std::cout << "Started." << std::endl;

while (true)
{
auto end = std::chrono::high_resolution_clock::now()
+ ${ComponentHelper.getExecutionIntervalMethod(comp)};
<#if ComponentHelper.isTimesync(comp)>
  cmp.compute();
</#if>
do {
std::this_thread::yield();
<#if ComponentHelper.isTimesync(comp)>
  std::this_thread::sleep_for(std::chrono::milliseconds(1));
<#else>
  std::this_thread::sleep_for(std::chrono::milliseconds(1000));
</#if>
} while (std::chrono::high_resolution_clock::now() < end);
}
return 0;
}