<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "commonCodePrefix")}
# silence useless warning about compiler changes
add_compile_options(${"$<$<CXX_COMPILER_ID:GNU>"}:-Wno-psabi>)

# include libraries
include_directories("${commonCodePrefix}lib/lib/raspberrypi")