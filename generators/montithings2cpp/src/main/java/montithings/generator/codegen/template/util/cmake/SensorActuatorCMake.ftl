<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("pckg", "port", "libraryPath", "config", "test", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

<#assign commonCodePrefix = "../">

cmake_minimum_required(VERSION 3.8.2)
project("${pckg}.${port}")
set(CMAKE_CXX_STANDARD 11)

<#if config.getSplittingMode().toString() != "OFF"
  && config.getTargetPlatform().toString() != "DSA_VCG"
  && config.getTargetPlatform().toString() != "DSA_LAB"
  && config.getMessageBroker().toString() == "DDS"> <#-- todo long expression-->
  # DDS specificcd
  find_package(OpenDDS REQUIRED)

  set(CMAKE_CXX_COMPILER "${r"${OPENDDS_COMPILER}"}")
  set(opendds_libs
    OpenDDS::Dcps # Core OpenDDS Library
    OpenDDS::InfoRepoDiscovery 
    OpenDDS::Tcp
    OpenDDS::Rtps #RTPS Discovery
    OpenDDS::Udp
    OpenDDS::Rtps_Udp
  )
</#if>

# Enable (more comfortable) debugging
set(CMAKE_CXX_FLAGS_DEBUG "${r"${CMAKE_CXX_FLAGS_DEBUG}"} -gdwarf-3")
set(CMAKE_C_FLAGS_DEBUG "${r"${CMAKE_C_FLAGS_DEBUG}"} -gdwarf-3")

<#if config.getTargetPlatform().toString() == "DSA_VCG"
|| config.getTargetPlatform().toString() == "DSA_LAB">
  ${tc.includeArgs("template.util.cmake.platform.dsa.Parameters", [config])}
</#if>
<#if config.getTargetPlatform().toString() == "RASPBERRY">
  ${tc.includeArgs("template.util.cmake.platform.raspberrypi.Parameters", [config, commonCodePrefix])}
  <#if config.getSplittingMode().toString() == "OFF">
    add_subdirectory(lib/lib/raspberrypi)
  </#if>
</#if>

set(PATH_CONAN_BUILD_INFO ${r"${CMAKE_BINARY_DIR}"}/conanbuildinfo.cmake)

if (EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
# Includes the contents of the conanbuildinfo.cmake file.
include(${r"${CMAKE_BINARY_DIR}"}/conanbuildinfo.cmake)
# Prepares the CMakeList.txt for Conan (set include directories, set variables, etc...)
conan_basic_setup()
endif()

<#assign needsNng = config.getTargetPlatform().toString() != "DSA_VCG"
&& config.getTargetPlatform().toString() != "DSA_LAB"
&& config.getSplittingMode().toString() != "OFF"
&& config.getMessageBroker().toString() == "OFF">
<#if needsNng>
  if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
  find_package(nng 1.3.0 CONFIG REQUIRED)
  endif ()
</#if>

find_package(Threads REQUIRED)

# for MSVC
if (MSVC)
set(variables
CMAKE_CXX_FLAGS_DEBUG
CMAKE_CXX_FLAGS_RELEASE
CMAKE_CXX_FLAGS_RELWITHDEBINFO
CMAKE_CXX_FLAGS_MINSIZEREL
)
foreach (variable ${r"${variables}"})
if (${r"${variable}"} MATCHES "/MD")
string(REGEX REPLACE "/MD" "/MT" ${r"${variable}"} "${"$"}{${"$"}{variable}}")
endif ()
endforeach ()
endif ()

#set target for building executables and libraries
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${r"${CMAKE_BINARY_DIR}"}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${r"${CMAKE_BINARY_DIR}"}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${r"${CMAKE_BINARY_DIR}"}/bin)

include_directories("${commonCodePrefix}${libraryPath?replace("\\","/")}")

# Include packages
file(GLOB_RECURSE ${pckg?upper_case}_SOURCES "*.cpp" "*.h")
list(FILTER ${pckg?upper_case}_SOURCES EXCLUDE REGEX "Deploy.*")
include_directories(".")

<#if config.getMessageBroker().toString() == "MQTT">
  if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
  # Include Mosquitto Library
  if(APPLE)
    find_library(MOSQUITTO_LIB mosquitto HINTS /usr/local/Cellar/mosquitto /opt/homebrew/Cellar/mosquitto)
  include_directories(/opt/homebrew/Cellar/mosquitto/2.0.10_1/include/ /opt/homebrew/Cellar/mosquitto/2.0.14/include/)
  elseif(WIN32)
    find_library(MOSQUITTO_LIB mosquitto HINTS C:\\Program\ Files\\Mosquitto\\devel)
    include_directories(C:\\Program\ Files\\Mosquitto\\devel)
else()
    find_library(MOSQUITTO_LIB mosquitto HINTS /snap/mosquitto/current/usr/lib)
    include_directories(/snap/mosquitto/current/usr/include)
  endif()
  endif()
</#if>

<#if test || config.getSplittingMode().toString() == "OFF">
  <#if !(config.getMessageBroker().toString() == "DDS")>
    set(EXCLUDE_DDS 1)
  </#if>
  <#if config.getMessageBroker().toString() != "MQTT"> <#-- todo invert -->
    set(EXCLUDE_MQTT 1)
  </#if>
  <#if !(config.getMessageBroker().toString() == "OFF" && config.getSplittingMode().toString() != "OFF")> <#-- todo invert splittingmode-->
    set(EXCLUDE_COMM_MANAGER 1)
  </#if>
</#if>

add_library(${pckg}_${port}Lib ${r"${SOURCES}"} ${r"${"}${pckg?upper_case}_SOURCES})
target_link_libraries(${pckg}_${port}Lib MontiThingsRTE)
<#if needsNng>
  if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
  target_link_libraries(${pckg}_${port}Lib nng::nng)
  endif()
</#if>
if (EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
target_link_libraries(${pckg}_${port}Lib ${r"${CONAN_LIBS}"})
endif()
target_link_libraries(${pckg}_${port}Lib Threads::Threads)
set_target_properties(${pckg}_${port}Lib PROPERTIES LINKER_LANGUAGE CXX)
install(TARGETS ${pckg}_${port}Lib DESTINATION ${r"${PROJECT_SOURCE_DIR}"}/lib)

<#if !test>
  add_executable(${pckg}.${port} Deploy${port}.cpp)
  target_link_libraries(${pckg}.${port} ${pckg}_${port}Lib)
  <#if config.getTargetPlatform().toString() == "DSA_VCG"
  || config.getTargetPlatform().toString() == "DSA_LAB">
      ${tc.includeArgs("template.util.cmake.platform.dsa.LinkLibraries", [pckg+"."+port])}
  <#elseif config.getTargetPlatform().toString() == "RASPBERRY">
      ${tc.includeArgs("template.util.cmake.platform.raspberrypi.LinkLibraries", [pckg+"."+port])}
  <#else>
    <#if config.getMessageBroker().toString() == "MQTT">
      if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
      target_link_libraries(${pckg}.${port} ${r"${MOSQUITTO_LIB}"})
      endif()
    <#elseif config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
      target_link_libraries(${pckg}.${port} "${r"${opendds_libs}"}")
    </#if>
    <#if needsNng>
      if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
      target_link_libraries(${pckg}.${port} nng::nng)
      endif()
    </#if>
  </#if>
  set_target_properties(${pckg}.${port} PROPERTIES LINKER_LANGUAGE CXX)
</#if>
