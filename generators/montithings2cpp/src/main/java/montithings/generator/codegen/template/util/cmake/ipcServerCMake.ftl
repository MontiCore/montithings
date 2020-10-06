${tc.signature("libraryPath","ipcPath","existsHWC","config")}
cmake_minimum_required(VERSION 3.8)
<#-- project(${port.getName()?cap_first}Server) TODO -->set(CMAKE_CXX_STANDARD 11)

<#if config.getTargetPlatform().toString() == "DSA_VCG"
|| config.getTargetPlatform().toString() == "DSA_LAB">
    ${tc.includeArgs("template.util.cmake.dsaParameters", [config])}
</#if>

<#if config.getTargetPlatform().toString() != "DSA_VCG"
&& config.getTargetPlatform().toString() != "DSA_LAB">
    find_package(nng 1.1.1 CONFIG REQUIRED)
</#if>

include_directories("${libraryPath?replace("\\","/")}")
include_directories(.)
file(GLOB SOURCES
"./*.cpp"
"./*.h"
"${libraryPath?replace("\\","/")}/*.cpp"
"${libraryPath?replace("\\","/")}/*.h")

<#-- add_executable(${port.getName()?cap_first}Server ${r"${SOURCES}"}) TODO --><#if config.getTargetPlatform().toString() == "DSA_VCG">
<#-- ${printDsaLinkLibraries(port.getName()?cap_first+"Server")} TODO -->
<#else>
<#-- target_link_libraries(${port.getName()?cap_first}Server nng::nng) TODO -->
</#if>