<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("files", "comp", "hwcPath", "libraryPath", "subPackagesPath", "config", "test")}

<#assign commonCodePrefix = "">
<#if config.getSplittingMode().toString() != "OFF">
    <#assign commonCodePrefix = "../">
</#if>

cmake_minimum_required(VERSION 3.8.2)
project("${comp.getFullName()}")
set(CMAKE_CXX_STANDARD 11)

<#if config.getSplittingMode().toString() != "OFF"
  && config.getTargetPlatform().toString() != "DSA_VCG"
  && config.getTargetPlatform().toString() != "DSA_LAB"
  && config.getMessageBroker().toString() == "DDS">
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

# Find all subdirectories with .h files
# Adapted from https://stackoverflow.com/a/31004567
MACRO(HEADER_DIRECTORIES input return_list)
FILE(GLOB_RECURSE new_list ${r"${input}"}/*.h)
SET(dir_list "")
FOREACH(file_path ${r"${new_list}"})
GET_FILENAME_COMPONENT(dir_path ${r"${file_path}"} PATH)
SET(dir_list ${r"${dir_list}"} ${r"${dir_path}"})
ENDFOREACH()
LIST(REMOVE_DUPLICATES dir_list)
SET(${r"${return_list}"} ${r"${dir_list}"})
ENDMACRO()
SET(dir_list "")

<#if config.getTargetPlatform().toString() == "DSA_VCG"
|| config.getTargetPlatform().toString() == "DSA_LAB">
    ${tc.includeArgs("template.util.cmake.dsaParameters", [config])}
</#if>

<#if config.getTargetPlatform().toString() != "DSA_VCG"
&& config.getTargetPlatform().toString() != "DSA_LAB">
  find_package(nng 1.1.1 CONFIG REQUIRED)
</#if>

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

<#-- include_directories("${hwcPath?replace("\\","/")}/${comp.getName()?uncap_first}") -->include_directories("${commonCodePrefix}${libraryPath?replace("\\","/")}")

# Include packages
<#list subPackagesPath as subdir>
  file(GLOB_RECURSE ${subdir.getName()?upper_case}_SOURCES "${subdir.getName()}/*.cpp" "${subdir.getName()}/*.h")
  include_directories("${subdir.getName()}")
</#list>

# Include HWC
file(GLOB_RECURSE HWC_SOURCES "hwc/*.cpp" "hwc/*.h")
HEADER_DIRECTORIES("hwc" dir_list)
include_directories("hwc" ${r"${dir_list}"})

# Include RTE
file(GLOB SOURCES "${commonCodePrefix}montithings-RTE/*.cpp" "${commonCodePrefix}montithings-RTE/*.h")


<#if config.getMessageBroker().toString() == "MQTT">
  # Include Mosquitto Library
  LINK_DIRECTORIES(/usr/local/Cellar/mosquitto/1.6.10/lib)
<#else>
  # exclude MQTT related part of the RTE to not require Mosquitto for compiling
  list(FILTER SOURCES EXCLUDE REGEX "montithings-RTE/Mqtt.*.h")
  list(FILTER SOURCES EXCLUDE REGEX "montithings-RTE/Mqtt.*.cpp")
</#if>

<#if config.getMessageBroker().toString() != "DDS">
  list(FILTER SOURCES EXCLUDE REGEX "montithings-RTE/DDS.*.h")
  list(FILTER SOURCES EXCLUDE REGEX "montithings-RTE/DDS.*.cpp")
  list(FILTER SOURCES EXCLUDE REGEX "montithings-RTE/DDSMessage.idl")
</#if>

<#if !test>
add_executable(${comp.getFullName()} ${r"${SOURCES}"} ${r"${HWC_SOURCES}"}
<#list subPackagesPath as subdir >
    ${r"${"}${subdir.getName()?upper_case}_SOURCES}
</#list>)
<#if config.getTargetPlatform().toString() == "DSA_VCG"
|| config.getTargetPlatform().toString() == "DSA_LAB">
    ${tc.includeArgs("template.util.cmake.dsaLinkLibraries", [comp.getFullName()])}
<#else>
  <#if config.getMessageBroker().toString() == "MQTT">
    target_link_libraries(${comp.getFullName()} mosquitto)
  <#elseif config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
    OPENDDS_TARGET_SOURCES(${comp.getFullName()} "../montithings-RTE/DDSMessage.idl")
    target_link_libraries(${comp.getFullName()} "${r"${opendds_libs}"}")
  </#if>
  target_link_libraries(${comp.getFullName()} nng::nng)
</#if>
set_target_properties(${comp.getFullName()} PROPERTIES LINKER_LANGUAGE CXX)
</#if>
