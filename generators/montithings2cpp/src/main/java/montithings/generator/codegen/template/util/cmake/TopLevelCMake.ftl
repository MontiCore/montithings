# (c) https://github.com/MontiCore/monticore
${tc.signature("files", "comp", "hwcPath", "libraryPath", "subPackagesPath", "config")}
<#--package montithings.generator.codegen.xtend.util

import java.io.File
import java.util.List
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams-->

<#assign commonCodePrefix = "">
if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
commonCodePrefix = "../"
}
return '''
cmake_minimum_required(VERSION 3.8)
project("${comp.getFullName()}")
set(CMAKE_CXX_STANDARD 11)

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
    ${printDsaParameters(config)}
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
string(REGEX REPLACE "/MD" "/MT" ${r"${variable}"} ${r"${${variable}}"})
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
    file(GLOB_RECURSE ${subdir.getName()?capitalize}_SOURCES "${subdir.getName()}/*.cpp" "${subdir.getName()}/*.h")
    include_directories("${subdir.getName()}")
</#list>

# Include HWC
file(GLOB_RECURSE HWC_SOURCES "hwc/*.cpp" "hwc/*.h")
HEADER_DIRECTORIES("hwc" dir_list)
include_directories("hwc" ${r"${dir_list}"})

# Include RTE
file(GLOB SOURCES "${commonCodePrefix}montithings-RTE/*.cpp" "${commonCodePrefix}montithings-RTE/*.h")


add_executable(${comp.getFullName()} ${r"${SOURCES}"} ${r"${HWC_SOURCES}"}
<#list subPackagesPath as subdir >
${r"${"}${subdir.getName()?capitalize}${r"}"}_SOURCES}
</#list>)
<#if config.getTargetPlatform().toString() == "DSA_VCG"
|| config.getTargetPlatform().toString() == "DSA_LAB">
    ${printDsaLinkLibraries(comp.getFullName())}
<#else>
    target_link_libraries(${comp.getFullName()} nng::nng)
</#if>
set_target_properties(${comp.getFullName()} PROPERTIES LINKER_LANGUAGE CXX)

  def static printDsaParameters(ConfigParams config) {
    # Cross compile
    set(CMAKE_SYSTEM_NAME Linux)
    set(CMAKE_SYSTEM_VERSION 1)
    <#if config.getTargetPlatform().toString() != "DSA_LAB">
    set(CMAKE_C_COMPILER   /usr/bin/powerpc-linux-gnu-gcc)
    set(CMAKE_CXX_COMPILER /usr/bin/powerpc-linux-gnu-g++)
    </#if>
    
    <#if config.getTargetPlatform().toString() == "DSA_VCG">
 find_library(ATOMIC_LIBRARY NAMES libatomic.a PATHS "/usr/lib/gcc/powerpc-linux-gnu/4.9")
 <#else>
 find_library(ATOMIC_LIBRARY NAMES libatomic.a PATHS "/usr/lib/gcc/powerpc-linux-gnu/4.9.2")
  </#if>

    <#if config.getTargetPlatform().toString() == "DSA_LAB">
    add_library(nng STATIC IMPORTED) 
    set_target_properties(nng PROPERTIES
      IMPORTED_LOCATION "/usr/powerpc-linux-gnu/lib/libnng.a"
      INTERFACE_INCLUDE_DIRECTORIES "/usr/powerpc-linux-gnu/include/nng"
    )
    </#if>
    
    file(GLOB_RECURSE INCLUDE_SOURCES "include/*.cpp" "include/*.h")
    HEADER_DIRECTORIES("inc" dir_list)
    include_directories("inc" ${r"${dir_list}"})

    link_directories(./lib/dsa-vcg)
  }
  
  def static printDsaLinkLibraries(String targetName) {
    target_link_libraries(${r"${targetName}"} nng pthread curl ${r"${ATOMIC_LIBRARY}"})
  }
    
  def static printIPCServerCMake(/*ResourcePortSymbol port,*/ String libraryPath, String ipcPath, Boolean existsHWC, ConfigParams config){
    cmake_minimum_required(VERSION 3.8)
<#-- project(${port.getName()?cap_first}Server) TODO -->set(CMAKE_CXX_STANDARD 11)

    <#if config.getTargetPlatform().toString() == "DSA_VCG"
     || config.getTargetPlatform().toString() == "DSA_LAB">
    ${printDsaParameters(config)}
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
  }