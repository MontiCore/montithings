# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import java.io.File
import java.util.List
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams-->

<#include "TopLevelCMake.ftl">

    def static printLinkTestLibraries(ComponentTypeSymbol comp, File[] subPackagesPath) {
      return '''

      add_library(${comp.getFullName().replaceAll("\\.","_")}Lib ${SOURCES} ${HWC_SOURCES}
      <#list subPackagesPath as subdir >
 ${${subdir.getName().toUpperCase()}_SOURCES}
 </#list>)
      target_link_libraries(${comp.getFullName().replaceAll("\\.","_")}Lib nng::nng)
      set_target_properties(${comp.getFullName().replaceAll("\\.","_")}Lib PROPERTIES LINKER_LANGUAGE CXX)
      install(TARGETS ${comp.getFullName().replaceAll("\\.","_")}Lib DESTINATION ${PROJECT_SOURCE_DIR}/lib)

      add_subdirectory(test/gtests)
      '''
    }
