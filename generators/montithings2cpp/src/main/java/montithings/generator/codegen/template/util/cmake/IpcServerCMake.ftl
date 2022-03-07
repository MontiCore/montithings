<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("libraryPath","ipcPath","existsHWC","config")}
<#include "/template/Preamble.ftl">

cmake_minimum_required(VERSION 3.8)
<#-- project(${port.getName()?cap_first}Server) TODO -->set(CMAKE_CXX_STANDARD 11)

<#if targetPlatformIsDsa>
    ${tc.includeArgs("template.util.cmake.platform.dsa.Parameters", [config])}
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

include_directories("${libraryPath?replace("\\","/")}")
include_directories(.)
file(GLOB SOURCES
"./*.cpp"
"./*.h"
"${libraryPath?replace("\\","/")}/*.cpp"
"${libraryPath?replace("\\","/")}/*.h")

<#-- add_executable(${port.getName()?cap_first}Server ${r"${SOURCES}"}) TODO --><#if targetPlatformIsDsaVcg>
<#-- ${printDsaLinkLibraries(port.getName()?cap_first+"Server")} TODO -->
<#else>
<#-- target_link_libraries(${port.getName()?cap_first}Server nng::nng) TODO -->
</#if>