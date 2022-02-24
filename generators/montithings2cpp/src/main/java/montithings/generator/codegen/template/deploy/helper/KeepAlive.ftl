<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

LOG(DEBUG) << "Started.";

<#if dummyName12>
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
  <#if brokerIsMQTT>
      mqttClientInstance->wait();
  </#if>

  <#if brokerIsDDS>
      while(true) {
          std::this_thread::yield();
          std::this_thread::sleep_for(std::chrono::seconds(1));
      }
  </#if>
</#if>