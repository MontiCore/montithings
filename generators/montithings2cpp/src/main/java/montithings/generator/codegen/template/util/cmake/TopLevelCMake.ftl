<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("files", "comp", "hwcPath", "libraryPath", "subPackagesPath", "config", "test", "sensorActuatorPorts", "existsHWC")}
<#include "/template/Preamble.ftl">

<#assign commonCodePrefix = "">
<#if !(splittingModeDisabled)>
    <#assign commonCodePrefix = "../">
</#if>

cmake_minimum_required(VERSION 3.8.2)
project("${comp.getFullName()}")
set(CMAKE_CXX_STANDARD 11)

if(NOT MSVC)
add_compile_options(-Wno-psabi)
endif()

<#if splittingModeDisabled>
  <#list sensorActuatorPorts as sensorActuatorPort >
    add_subdirectory ("${sensorActuatorPort}")
  </#list>
</#if>


<#if needsDDS>
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

if(${r"${CMAKE_HOST_SYSTEM_NAME}"} STREQUAL Darwin)
# Enable (more comfortable) debugging
set(CMAKE_CXX_FLAGS_DEBUG "${r"${CMAKE_CXX_FLAGS_DEBUG}"} -gdwarf-3")
set(CMAKE_C_FLAGS_DEBUG "${r"${CMAKE_C_FLAGS_DEBUG}"} -gdwarf-3")
endif()

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

<#if targetPlatformIsDsa>
  ${tc.includeArgs("template.util.cmake.platform.dsa.Parameters", [config])}
</#if>
<#if targetPlatformIsRaspberry>
  ${tc.includeArgs("template.util.cmake.platform.raspberrypi.Parameters", [config, commonCodePrefix])}
  <#if splittingModeDisabled>
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
<#list subPackagesPath as subdir>
  file(GLOB_RECURSE ${subdir.getName()?upper_case}_SOURCES "${subdir.getName()}/*.cpp" "${subdir.getName()}/*.h")
  list(FILTER ${subdir.getName()?upper_case}_SOURCES EXCLUDE REGEX "${Utils.getDeployFile(comp)}")
  include_directories("${subdir.getName()}")
  HEADER_DIRECTORIES("${subdir.getName()}" ${subdir.getName()}_HEADERS)
  include_directories(${r"${"}${subdir.getName()}${r"_HEADERS}"})
</#list>

# Include Subcomponent Headers
<#list ComponentHelper.getSubcompTypesRecursive(comp) as subcomp>
  <#assign subcompName = subcomp.getFullName()>
  # HEADER_DIRECTORIES("../${subcompName}" ${subcompName?replace(".","_")}_HEADER)
  # include_directories("../${subcompName}" ${"$"}{${subcompName?replace(".","_")}_HEADER})
</#list>

# Include HWC
file(GLOB_RECURSE HWC_SOURCES "hwc/*.cpp" "hwc/*.h")
HEADER_DIRECTORIES("hwc" dir_list)
include_directories("hwc" ${r"${dir_list}"})

<#if brokerIsMQTT>
  if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
  # Include Mosquitto Library
  if(APPLE)
    execute_process (
      COMMAND bash -c "brew --prefix mosquitto"
      OUTPUT_VARIABLE MOSQUITTO_PREFIX
    )
    find_library(MOSQUITTO_LIB mosquitto HINTS ${r"${MOSQUITTO_PREFIX}"})
    execute_process (
      COMMAND bash -c "echo $(brew --prefix mosquitto)/include"
      OUTPUT_VARIABLE MOSQUITTO_INCLUDE
    )
    include_directories("${r"${MOSQUITTO_INCLUDE}"}")
  elseif(WIN32)
    find_library(MOSQUITTO_LIB mosquitto HINTS C:\\Program\ Files\\Mosquitto\\devel)
    include_directories(C:\\Program\ Files\\Mosquitto\\devel)
  else()
    find_library(MOSQUITTO_LIB mosquitto HINTS /snap/mosquitto/current/usr/lib)
    include_directories(/snap/mosquitto/current/usr/include)
  endif()
  endif()
</#if>

<#if test || splittingModeDisabled>
  <#if !(brokerIsDDS)>
    set(EXCLUDE_DDS 1)
  </#if>
  <#if !(brokerIsMQTT)>
    set(EXCLUDE_MQTT 1)
  </#if>
  <#if !(brokerDisabled && !(splittingModeDisabled))>
    set(EXCLUDE_COMM_MANAGER 1)
  </#if>
  <#if (logTracingEnabled)>
    set(ENABLE_LOG_TRACING 1)
  </#if>
  add_subdirectory(montithings-RTE)
</#if>

<#if needsProtobuf>
  if (EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
    find_package(Protobuf)
  else()
    find_package(Protobuf 3 REQUIRED)
  endif()
  file(GLOB_RECURSE PROTOBUF_SOURCES ${r"${CMAKE_SOURCE_DIR}"}/*.proto)
  protobuf_generate_cpp(PROTOBUF_CPP_SOURCES PROTOBUF_CPP_HEADERS ${r"${PROTOBUF_SOURCES}"})
  # Include directory with generated .pb.h files
  include_directories(${r"${CMAKE_CURRENT_BINARY_DIR}"})
</#if>

file(GLOB SOURCES "*.cpp")
list(FILTER SOURCES EXCLUDE REGEX "${Utils.getDeployFile(comp)}")
add_library(${comp.getFullName()?replace(".","_")}Lib ${r"${SOURCES}"} ${r"${PROTOBUF_CPP_SOURCES}"} ${r"${HWC_SOURCES}"}
<#list subPackagesPath as subdir >
${r"${"}${subdir.getName()?upper_case}_SOURCES}
</#list>)
target_link_libraries(${comp.getFullName()?replace(".","_")}Lib MontiThingsRTE)
<#if needsNng>
  if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
  target_link_libraries(${comp.getFullName()?replace(".","_")}Lib nng::nng)
  endif()
</#if>
<#if needsProtobuf>
  target_link_libraries(${comp.getFullName()?replace(".","_")}Lib protobuf::libprotobuf)
</#if>
if (EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
target_link_libraries(${comp.getFullName()?replace(".","_")}Lib ${r"${CONAN_LIBS}"})
endif()
target_link_libraries(${comp.getFullName()?replace(".","_")}Lib Threads::Threads)
set_target_properties(${comp.getFullName()?replace(".","_")}Lib PROPERTIES LINKER_LANGUAGE CXX)
install(TARGETS ${comp.getFullName()?replace(".","_")}Lib DESTINATION ${r"${PROJECT_SOURCE_DIR}"}/lib)

<#if !test>
  add_executable(${comp.getFullName()} ${Utils.getDeployFile(comp)})
  target_link_libraries(${comp.getFullName()} ${comp.getFullName()?replace(".","_")}Lib)
  <#if targetPlatformIsDsa>
      ${tc.includeArgs("template.util.cmake.platform.dsa.LinkLibraries", [comp.getFullName()])}
  <#elseif targetPlatformIsRaspberry>
      ${tc.includeArgs("template.util.cmake.platform.raspberrypi.LinkLibraries", [comp.getFullName()])}
  <#else>
    <#if brokerIsMQTT>
      if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
      target_link_libraries(${comp.getFullName()} ${r"${MOSQUITTO_LIB}"})
      endif()
    <#elseif !(splittingModeDisabled) && brokerIsDDS>
      target_link_libraries(${comp.getFullName()} "${r"${opendds_libs}"}")
    </#if>
    <#if needsNng>
      if (NOT EXISTS ${r"${PATH_CONAN_BUILD_INFO}"})
      target_link_libraries(${comp.getFullName()} nng::nng)
      endif()
    </#if>
  </#if>
  set_target_properties(${comp.getFullName()} PROPERTIES LINKER_LANGUAGE CXX)
</#if>

<#if test>
  macro(package_add_test TESTNAME)
  add_executable(${"$"}{TESTNAME} ${"$"}{ARGN})
  target_link_libraries(${"$"}{TESTNAME} gtest gmock gtest_main)
  target_link_libraries(${"$"}{TESTNAME} ${comp.getFullName()?replace(".","_")}Lib)
  add_test(NAME ${"$"}{TESTNAME} COMMAND ${"$"}{TESTNAME})
  set_target_properties(${"$"}{TESTNAME} PROPERTIES FOLDER tests)
  endmacro()
</#if>
