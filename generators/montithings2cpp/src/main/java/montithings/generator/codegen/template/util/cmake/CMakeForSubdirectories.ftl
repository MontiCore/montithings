<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("subdirectories", "sensorActuatorPorts", "config", "existsHWC")}
<#include "/template/ConfigPreamble.ftl">

cmake_minimum_required (VERSION 3.8)
project ("MontiThings Application")

add_compile_options(-Wno-psabi)
<#if !(brokerIsDDS)>
  set(EXCLUDE_DDS 1)
</#if>
<#if !(brokerIsMQTT)>
  set(EXCLUDE_MQTT 1)
</#if>
<#if !(splittingModeDisabled || brokerDisabled)>
  set(EXCLUDE_COMM_MANAGER 1)
</#if>
<#if (logTracingEnabled)>
  set(ENABLE_LOG_TRACING 1)
</#if>
<#if targetPlatformIsRaspberry>
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