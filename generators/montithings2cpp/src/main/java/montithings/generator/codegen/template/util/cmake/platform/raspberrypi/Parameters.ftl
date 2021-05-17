<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config")}
# silence useless warning about compiler changes
add_compile_options(${"$<$<CXX_COMPILER_ID:GNU>"}:-Wno-psabi>)

# include libraries
include_directories("lib/lib/raspberrypi")
add_subdirectory(lib/lib/raspberrypi)