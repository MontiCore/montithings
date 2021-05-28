<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getLogTracing().toString() == "ON" && !(comp.getPorts()?size == 0)>
    ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name}LogTraceObserver logTraceObserver(&cmp);
</#if>

LOG(DEBUG) << "Started.";

<#if ComponentHelper.isTimesync(comp) || (config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF")>
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

  <#if config.getMessageBroker().toString() == "DDS">
      while(true) {
          std::this_thread::yield();
          std::this_thread::sleep_for(std::chrono::seconds(1));
      }
  </#if>
</#if>