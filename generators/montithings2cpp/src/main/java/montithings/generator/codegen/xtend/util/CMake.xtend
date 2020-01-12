// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import java.io.File
import montiarc._symboltable.ComponentSymbol
import montithings._symboltable.ResourcePortSymbol
import montithings.generator.helper.ComponentHelper

class CMake {
	
	def static printTopLevelCMake(File[] files, ComponentSymbol comp, String hwcPath, String libraryPath, File[] subPackagesPath) {
		return '''
		cmake_minimum_required(VERSION 3.8)
		project("«comp.name»")
		set(CMAKE_CXX_STANDARD 11)
		
		find_package(nng 1.1.1 CONFIG REQUIRED)
		find_package(Boost) 

		include_directories(${Boost_INCLUDE_DIRS})
		
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
		include_directories("«libraryPath.replace("\\","/")»") 
		
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
		file(GLOB SOURCES "montithings-RTE/*.cpp" "montithings-RTE/*.h")
		
		add_executable(«comp.name» ${SOURCES} ${HWC_SOURCES} 
		«FOR subdir : subPackagesPath»
		${«subdir.name.toUpperCase()»_SOURCES}
		«ENDFOR»)
		target_link_libraries(«comp.name» nng::nng Boost::boost)
		set_target_properties(«comp.name» PROPERTIES LINKER_LANGUAGE CXX)
		'''
	}
		
	def static printIPCServerCMake(ResourcePortSymbol port, String libraryPath, String ipcPath, Boolean existsHWC){
		return 
		'''
		cmake_minimum_required(VERSION 3.12)
		project(«port.name.toFirstUpper»Server)
		
		set(CMAKE_CXX_STANDARD 11)
		
		find_package(nng 1.1.1 CONFIG REQUIRED)
		find_package(Boost) 
		
		«IF existsHWC»
		include_directories(«ipcPath.replace("\\","/")»)
		«ENDIF»
		include_directories(${Boost_INCLUDE_DIRS}) 
		include_directories("«libraryPath.replace("\\","/")»")
		include_directories(.)
		file(GLOB SOURCES 
		"./*.cpp"
		"./*.h"
		«IF existsHWC»
		"«ipcPath.replace("\\","/")»/*.cpp"
		"«ipcPath.replace("\\","/")»/*.h"
		«ENDIF»
		"«libraryPath.replace("\\","/")»/*.cpp"
		"«libraryPath.replace("\\","/")»/*.h")
		
		add_executable(«port.name.toFirstUpper»Server ${SOURCES})
		target_link_libraries(«port.name.toFirstUpper»Server nng::nng Boost::boost)
		'''
	}
	
}