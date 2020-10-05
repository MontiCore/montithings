<#-- (c) https://github.com/MontiCore/monticore -->
<#include "TopLevelCMake.ftl">

add_library(${comp.getFullName()?replace(".","_")}Lib ${"$"}{SOURCES} ${"$"}{HWC_SOURCES}
<#list subPackagesPath as subdir >
  ${"$"}{${subdir.getName()?upper_case}_SOURCES}
</#list>)
target_link_libraries(${comp.getFullName()?replace(".","_")}Lib nng::nng)
set_target_properties(${comp.getFullName()?replace(".","_")}Lib PROPERTIES LINKER_LANGUAGE CXX)
install(TARGETS ${comp.getFullName()?replace(".","_")}Lib DESTINATION ${"$"}{PROJECT_SOURCE_DIR}/lib)

add_subdirectory(test/gtests)