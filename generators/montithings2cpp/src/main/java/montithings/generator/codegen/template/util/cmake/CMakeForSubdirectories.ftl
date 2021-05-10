<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("subdirectories", "config", "existsHWC")}

cmake_minimum_required (VERSION 3.8)
project ("MontiThings Application")
<#if !(config.getMessageBroker().toString() == "DDS")>
  set(EXCLUDE_DDS 1)
</#if>
<#if !(config.getMessageBroker().toString() == "MQTT")>
  set(EXCLUDE_MQTT 1)
</#if>
<#if (config.getSplittingMode().toString() != "OFF") && (config.getMessageBroker().toString() != "OFF")>
  set(EXCLUDE_COMM_MANAGER 1)
</#if>
add_subdirectory ("montithings-RTE")
<#list subdirectories as subdir >
  add_subdirectory ("${subdir}")
</#list>