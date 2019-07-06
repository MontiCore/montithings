package de.montiarcautomaton.generator.codegen.xtend.util

import java.io.File
import java.util.List
import java.util.ArrayList
import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper

class CMake {
	
	def static printCMake(File[] files, ComponentSymbol comp, File hwcPath, File libraryPath) {
		
		
		return '''
		cmake_minimum_required(VERSION 3.14)
		project(«comp.name»)
		
		set(CMAKE_CXX_STANDARD 14)
		
		include_directories("«hwcPath.absolutePath.replace("\\","/")»/«comp.name.toFirstLower»")
		include_directories(«libraryPath.absolutePath.replace("\\","/")»)
		include_directories(.)
		file(GLOB SOURCES 
		"./*.cpp"
		"./*.h"
		"«hwcPath.absolutePath.replace("\\","/")»/«comp.name.toFirstLower»/*.cpp"
		"«hwcPath.absolutePath.replace("\\","/")»/«comp.name.toFirstLower»/*.h"
		"«libraryPath.absolutePath.replace("\\","/")»/«comp.name.toFirstLower»/*.cpp"
		"«libraryPath.absolutePath.replace("\\","/")»/«comp.name.toFirstLower»/*.h")
		
		add_executable(«comp.name» ${SOURCES})
		'''
		}
	
}