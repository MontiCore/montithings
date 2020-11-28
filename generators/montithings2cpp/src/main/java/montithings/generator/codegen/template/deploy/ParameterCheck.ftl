<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getSplittingMode().toString() != "DISTRIBUTED" && config.getMessageBroker().toString() == "MQTT">
  if (argc != 4 && argc != 2)
<#elseif config.getSplittingMode().toString() == "OFF" && config.getMessageBroker().toString() == "OFF">
  if (argc != 2)
<#elseif config.getMessageBroker().toString() == "DDS" && config.getSplittingMode().toString() == "DISTRIBUTED">
  if (argc != 6)
<#else>
  if (argc != 4)
</#if>
{
std::cerr << "Called with wrong number of arguments. Please provide the following arguments:" << std::endl;
std::cerr << "1) The component's instance name" << std::endl;
<#if config.getSplittingMode().toString() == "LOCAL" && config.getMessageBroker().toString() != "MQTT" && config.getMessageBroker().toString() != "DDS">
  std::cerr << "2) Network port for management traffic" << std::endl;
  std::cerr << "3) Network port for data traffic" << std::endl;
<#elseif config.getMessageBroker().toString() == "MQTT">
  std::cerr << "2) Hostname (or IP address) of the MQTT broker" << std::endl;
  std::cerr << "3) Port of the MQTT broker" << std::endl;
  <#if config.getSplittingMode().toString() == "OFF">
    std::cerr << std::endl;
    std::cerr << "Arguments 2 and 3 are optional but if provided have to be provided together." << std::endl;
  </#if>
<#elseif config.getMessageBroker().toString() == "DDS">
  std::cerr << "2) DCPSConfigFile (e.g. -DCPSConfigFile dcpsconfig.ini)" << std::endl;
  <#if config.getSplittingMode().toString() == "DISTRIBUTED">
    std::cerr << "3) DCPSInfoRepo (e.g. -DCPSInfoRepo localhost:12345)" << std::endl;
  </#if>
</#if>
std::cerr << std::endl;
std::cerr << "Aborting." << std::endl;
exit(1);
}