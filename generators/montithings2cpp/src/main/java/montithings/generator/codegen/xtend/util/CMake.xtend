// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import java.io.File
import java.util.List
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams

class CMake {
  
  def static printDsaParameters() {
    return '''
    # Cross compile
    set(CMAKE_SYSTEM_NAME Linux)
    set(CMAKE_SYSTEM_VERSION 1)
    set(CMAKE_C_COMPILER   /usr/bin/powerpc-linux-gnu-gcc)
    set(CMAKE_CXX_COMPILER /usr/bin/powerpc-linux-gnu-g++)
    
    find_library(ATOMIC_LIBRARY NAMES libatomic.a PATHS "/usr/lib/gcc/powerpc-linux-gnu/4.9")
    
    file(GLOB_RECURSE INCLUDE_SOURCES "include/*.cpp" "include/*.h")
    HEADER_DIRECTORIES("inc" dir_list)
    include_directories("inc" ${dir_list})

    link_directories(./lib/dsa-vcg)
    '''
  }
  
  def static printDsaLinkLibraries(String targetName) {
    return '''
    target_link_libraries(«targetName» nng pthread curl ${ATOMIC_LIBRARY})
    '''
  }

  def static printGoogleTestParameters(ComponentTypeSymbol comp) {
    return '''
    project(gtests)

    enable_testing()

    add_subdirectory(lib)
    include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})

    macro(package_add_test TESTNAME)
        add_executable(${TESTNAME} ${ARGN})
        target_link_libraries(${TESTNAME} gtest gmock gtest_main)
        target_link_libraries(${TESTNAME} «comp.fullName.replaceAll("\\.","_")»Lib)
        add_test(NAME ${TESTNAME} COMMAND ${TESTNAME})
        set_target_properties(${TESTNAME} PROPERTIES FOLDER tests)
    endmacro()

    package_add_test(«comp.fullName.replaceAll("\\.","_")»TestSuite «comp.fullName.replaceAll("\\.","_")»Test.cpp)
    include_directories("/usr/local/include")
  '''
  }

  def static printCMakeForSubdirectories(List<String> subdirectories) {
    return '''
    cmake_minimum_required (VERSION 3.8)
    project ("MontiThings Application")
    «FOR subdir : subdirectories»
    add_subdirectory ("«subdir»")
    «ENDFOR»
    '''
  }
  
  def static printTopLevelCMake(File[] files, ComponentTypeSymbol comp, String hwcPath, String libraryPath, File[] subPackagesPath, ConfigParams config) {
    var commonCodePrefix = ""
    if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
      commonCodePrefix = "../"
    }
    return '''
    cmake_minimum_required(VERSION 3.8)
    project("«comp.fullName»")
    set(CMAKE_CXX_STANDARD 11)

    # Enable (more comfortable) debugging
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -gdwarf-3")
    set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -gdwarf-3")
    
    # Find all subdirectories with .h files
    # Adapted from https://stackoverflow.com/a/31004567
    MACRO(HEADER_DIRECTORIES input return_list)
        FILE(GLOB_RECURSE new_list ${input}/*.h)
        SET(dir_list "")
        FOREACH(file_path ${new_list})
            GET_FILENAME_COMPONENT(dir_path ${file_path} PATH)
            SET(dir_list ${dir_list} ${dir_path})
        ENDFOREACH()
        LIST(REMOVE_DUPLICATES dir_list)
        SET(${return_list} ${dir_list})
    ENDMACRO()
    SET(dir_list "")
    
    «IF config.getTargetPlatform() == ConfigParams.TargetPlatform.DSA_VCG»
    «printDsaParameters()»
    «ENDIF»

    «IF config.getTargetPlatform() != ConfigParams.TargetPlatform.DSA_VCG»
    find_package(nng 1.1.1 CONFIG REQUIRED)
    «ENDIF»
    
    # for MSVC
    if (MSVC)
        set(variables
                CMAKE_CXX_FLAGS_DEBUG
                CMAKE_CXX_FLAGS_RELEASE
                CMAKE_CXX_FLAGS_RELWITHDEBINFO
                CMAKE_CXX_FLAGS_MINSIZEREL
                )
        foreach (variable ${variables})
            if (${variable} MATCHES "/MD")
                string(REGEX REPLACE "/MD" "/MT" ${variable} "${${variable}}")
            endif ()
        endforeach ()
    endif ()
    
    #set target for building executables and libraries
    set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
    set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
    set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
    
«««		include_directories("«hwcPath.replace("\\","/")»/«comp.name.toFirstLower»")
    include_directories("«commonCodePrefix»«libraryPath.replace("\\","/")»")
    
    # Include packages
    «FOR subdir : subPackagesPath»
    file(GLOB_RECURSE «subdir.name.toUpperCase()»_SOURCES "«subdir.name»/*.cpp" "«subdir.name»/*.h")
    include_directories("«subdir.name»")
    «ENDFOR»
    
    # Include HWC
    file(GLOB_RECURSE HWC_SOURCES "hwc/*.cpp" "hwc/*.h")
    HEADER_DIRECTORIES("hwc" dir_list)
    include_directories("hwc" ${dir_list})
    
    # Include RTE
    file(GLOB SOURCES "«commonCodePrefix»montithings-RTE/*.cpp" "«commonCodePrefix»montithings-RTE/*.h")


    add_executable(«comp.fullName» ${SOURCES} ${HWC_SOURCES} 
    «FOR subdir : subPackagesPath»
    ${«subdir.name.toUpperCase()»_SOURCES}
    «ENDFOR»)
    «IF config.getTargetPlatform() == ConfigParams.TargetPlatform.DSA_VCG»
    «printDsaLinkLibraries(comp.fullName)»
    «ELSE»
    target_link_libraries(«comp.fullName» nng::nng)
    «ENDIF»
    set_target_properties(«comp.fullName» PROPERTIES LINKER_LANGUAGE CXX)
    '''
  }

    def static printLinkTestLibraries(ComponentTypeSymbol comp, File[] subPackagesPath) {
      return '''

      add_library(«comp.fullName.replaceAll("\\.","_")»Lib ${SOURCES} ${HWC_SOURCES}
      «FOR subdir : subPackagesPath»
      ${«subdir.name.toUpperCase()»_SOURCES}
      «ENDFOR»)
      target_link_libraries(«comp.fullName.replaceAll("\\.","_")»Lib nng::nng)
      set_target_properties(«comp.fullName.replaceAll("\\.","_")»Lib PROPERTIES LINKER_LANGUAGE CXX)
      install(TARGETS «comp.fullName.replaceAll("\\.","_")»Lib DESTINATION ${PROJECT_SOURCE_DIR}/lib)

      add_subdirectory(test/gtests)
      '''
    }
    
  def static printIPCServerCMake(/*ResourcePortSymbol port,*/ String libraryPath, String ipcPath, Boolean existsHWC, ConfigParams config){
    return 
    '''
    cmake_minimum_required(VERSION 3.8)
«««		project(«port.name.toFirstUpper»Server) TODO
    
    set(CMAKE_CXX_STANDARD 11)

    «IF config.getTargetPlatform() == ConfigParams.TargetPlatform.DSA_VCG»
    «printDsaParameters()»
    «ENDIF»
    
    «IF config.getTargetPlatform() != ConfigParams.TargetPlatform.DSA_VCG»
    find_package(nng 1.1.1 CONFIG REQUIRED)
    «ENDIF»
    
    include_directories("«libraryPath.replace("\\","/")»")
    include_directories(.)
    file(GLOB SOURCES 
    "./*.cpp"
    "./*.h"
    "«libraryPath.replace("\\","/")»/*.cpp"
    "«libraryPath.replace("\\","/")»/*.h")
    
«««		add_executable(«port.name.toFirstUpper»Server ${SOURCES}) TODO
    «IF config.getTargetPlatform() == ConfigParams.TargetPlatform.DSA_VCG»
«««		«printDsaLinkLibraries(port.name.toFirstUpper+"Server")» TODO
    «ELSE»
«««		target_link_libraries(«port.name.toFirstUpper»Server nng::nng) TODO
    «ENDIF»
    '''
  }
  
}