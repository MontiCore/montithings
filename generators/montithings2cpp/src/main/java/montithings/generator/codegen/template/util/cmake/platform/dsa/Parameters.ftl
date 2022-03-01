<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config")}
<#include "/template/ConfigPreamble.ftl">

# Cross compile
set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_SYSTEM_VERSION 1)
<#if !(targetPlatformIsDsaLab)>
    set(CMAKE_C_COMPILER   /usr/bin/powerpc-linux-gnu-gcc)
    set(CMAKE_CXX_COMPILER /usr/bin/powerpc-linux-gnu-g++)
</#if>

<#if targetPlatformIsDsaVcg>
    find_library(ATOMIC_LIBRARY NAMES libatomic.a PATHS "/usr/lib/gcc/powerpc-linux-gnu/4.9")
<#else>
    find_library(ATOMIC_LIBRARY NAMES libatomic.a PATHS "/usr/lib/gcc/powerpc-linux-gnu/4.9.2")
</#if>

<#if targetPlatformIsDsaLab>
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