<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${compname}.h"
<#if config.getSplittingMode().toString() != "OFF">
  #include "${compname}Manager.h"
</#if>
#include ${"<chrono>"}
#include ${"<thread>"}

int main(int argc, char* argv[])
{
<#if config.getSplittingMode().toString() == "LOCAL">
  if (argc != 4 && argc != 6)
  {
  std::cerr << "Called with wrong number of arguments. Please provide the following arguments:" << std::endl;
  std::cerr << "1) The component's instance name" << std::endl;
  std::cerr << "2) Network port for management traffic" << std::endl;
  std::cerr << "3) Network port for data traffic" << std::endl;
  std::cerr << "4) Hostname (or IP address) of the MQTT broker" << std::endl;
  std::cerr << "5) Port of the MQTT broker" << std::endl;
  std::cerr << std::endl;
  std::cerr << "Arguments 4 and 5 are optional but if provided have to be provided together." << std::endl;
  std::cerr << std::endl;
  std::cerr << "Aborting." << std::endl;
  exit(1);
  }
</#if>
<#if config.getSplittingMode().toString() != "LOCAL">
  <#if config.getMessageBroker().toString() == "MQTT">
    <#if config.getSplittingMode().toString() == "DISTRIBUTED">
      if (argc != 4)
    <#elseif config.getSplittingMode().toString() == "OFF">
      if (argc != 4 && argc != 2)
    </#if>
  <#else>
  if (argc != 2)
  </#if>
  {
  std::cerr << "Called with wrong number of arguments. Please provide the following arguments:" << std::endl;
  std::cerr << "1) The component's instance name" << std::endl;
  <#if config.getMessageBroker().toString() == "MQTT">
    std::cerr << "2) Hostname (or IP address) of the MQTT broker" << std::endl;
    std::cerr << "3) Port of the MQTT broker" << std::endl;
    std::cerr << std::endl;
    std::cerr << "Arguments 2 and 3 are optional but if provided have to be provided together." << std::endl;
    std::cerr << std::endl;
  </#if>
  std::cerr << std::endl;
  std::cerr << "Aborting." << std::endl;
  exit(1);
  }
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
  <#if config.getSplittingMode().toString() == "LOCAL">
    if (argc == 6) { MqttClient::instance(argv[4], std::stoi(argv[5])); }
  <#else>
    if (argc == 4) { MqttClient::instance(argv[2], std::stoi(argv[3])); }
  </#if>
    else
    {
    std::cerr << "WARNING: No MQTT hostname and port provided as argument. Defaulting to localhost:1883." << std::endl;
    MqttClient::instance();
    }
</#if>

${ComponentHelper.printPackageNamespaceForComponent(comp)}${compname} cmp (argv[1]);
<#if config.getSplittingMode().toString() != "OFF">
    ${ComponentHelper.printPackageNamespaceForComponent(comp)}${compname}Manager manager (&cmp, argv[2], argv[3]);
  manager.initializePorts ();
    <#if comp.isDecomposed()>
      manager.searchSubcomponents ();
    </#if>
</#if>

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