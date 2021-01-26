<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

std::cout << "Started." << std::endl;

<#if ComponentHelper.isTimesync(comp) || (config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() != "MQTT")>
  while (true)
  {
  auto end = std::chrono::high_resolution_clock::now()
  + ${ComponentHelper.getExecutionIntervalMethod(comp)};
  cmp.compute();
  do {
  std::this_thread::yield();
  std::this_thread::sleep_for(std::chrono::milliseconds(1));
  } while (std::chrono::high_resolution_clock::now() < end);
  }
<#else>
  cmp.threadJoin();
  <#if config.getMessageBroker().toString() == "MQTT">
    MqttClient::instance()->wait();
  </#if>
</#if>