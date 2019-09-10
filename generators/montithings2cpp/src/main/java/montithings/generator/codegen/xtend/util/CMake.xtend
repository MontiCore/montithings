package montithings.generator.codegen.xtend.util

import java.io.File
import montiarc._symboltable.ComponentSymbol
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