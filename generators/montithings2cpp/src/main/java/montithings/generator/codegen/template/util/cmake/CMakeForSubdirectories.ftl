<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("subdirectories", "sensorActuatorPorts", "config", "existsHWC")}

cmake_minimum_required (VERSION 3.8)
project ("MontiThings Application")

add_compile_options(-Wno-psabi)

<#if !(config.getMessageBroker().toString() == "DDS")>
  set(EXCLUDE_DDS 1)
</#if>
<#if !(config.getMessageBroker().toString() == "MQTT")>
  set(EXCLUDE_MQTT 1)
</#if>
<#if (config.getSplittingMode().toString() != "OFF") && (config.getMessageBroker().toString() != "OFF")>
  set(EXCLUDE_COMM_MANAGER 1)
</#if>
<#if (config.getLogTracing().toString() == "ON")>
  set(ENABLE_LOG_TRACING 1)
</#if>
<#if config.getTargetPlatform().toString() == "RASPBERRY">
  add_compile_options(${"$<$<CXX_COMPILER_ID:GNU>"}:-Wno-psabi>)
  add_subdirectory(lib/lib/raspberrypi)
</#if>
add_subdirectory ("montithings-RTE")
<#list subdirectories as subdir >
  add_subdirectory ("${subdir}")
</#list>
<#list sensorActuatorPorts as sensorActuatorPort >
  add_subdirectory ("${sensorActuatorPort}")
</#list>