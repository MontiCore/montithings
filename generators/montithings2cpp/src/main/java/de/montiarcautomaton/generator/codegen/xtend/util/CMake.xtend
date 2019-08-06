package de.montiarcautomaton.generator.codegen.xtend.util

import java.io.File
import java.util.List
import java.util.ArrayList
import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper
import montithings._symboltable.ResourcePortSymbol

class CMake {
	
	def static printCMake(File[] files, ComponentSymbol comp, String hwcPath, String libraryPath) {
		
		
		return '''
		cmake_minimum_required(VERSION 3.12)
		project(«comp.name»)
		
		set(CMAKE_CXX_STANDARD 11)
		
		find_package(nng 1.1.1 CONFIG REQUIRED)
		find_package(Boost) 
		
		include_directories(${Boost_INCLUDE_DIRS}) 
		include_directories("«hwcPath.replace("\\","/")»/«comp.name.toFirstLower»")
		include_directories("«libraryPath.replace("\\","/")»")
		include_directories(.)
		file(GLOB SOURCES 
		"./*.cpp"
		"./*.h"
		"«hwcPath.replace("\\","/")»/«comp.name.toFirstLower»/*.cpp"
		"«hwcPath.replace("\\","/")»/«comp.name.toFirstLower»/*.h"
		"«libraryPath.replace("\\","/")»/*.cpp"
		"«libraryPath.replace("\\","/")»/*.h")
		
		add_executable(«comp.name» ${SOURCES})
		target_link_libraries(«comp.name» nng::nng Boost::boost)
		'''
		}
		
	def static printIPCServerCMake(ResourcePortSymbol port, String libraryPath){
		return 
		'''
		cmake_minimum_required(VERSION 3.12)
		project(«port.name»Server)
		
		set(CMAKE_CXX_STANDARD 11)
		
		find_package(nng 1.1.1 CONFIG REQUIRED)
		
		include_directories("«libraryPath.replace("\\","/")»")
		include_directories(.)
		file(GLOB SOURCES 
		"./*.cpp"
		"./*.h"
		"«libraryPath.replace("\\","/")»/*.cpp"
		"«libraryPath.replace("\\","/")»/*.h")
		
		add_executable(«port.name»Server ${SOURCES})
		target_link_libraries(«port.name»Server nng::nng)
		'''
	}
	
}