package de.montiarcautomaton.generator.codegen.xtend

import montiarc._symboltable.ComponentSymbol

class Deploy {
	
	def static generateDeploy(ComponentSymbol comp) {
		return '''
		#include "«comp.name».h"
		#include <chrono>
		#include <thread>
		
		
		int main()
		{
			«comp.name» cmp;
			cmp.setUp();
			cmp.init();
			«IF !comp.getStereotype().containsKey("timesync")»
			cmp.start();
			«ENDIF»
		
			while (true)
			{
				auto end = std::chrono::high_resolution_clock::now() + std::chrono::milliseconds(50);
				//TODO:
				«IF comp.getStereotype().containsKey("timesync")»
				cmp.compute();
				«ENDIF» 
				do {
				        std::this_thread::yield();
				        «IF comp.getStereotype().containsKey("timesync")»
                        std::this_thread::sleep_for(std::chrono::milliseconds(1));
                        «ELSE»
                        std::this_thread::sleep_for(std::chrono::milliseconds(1000));
                        «ENDIF» 
				        
				    } while (std::chrono::high_resolution_clock::now()  < end);
			}
		
		
			return 0;
		}
		
		'''
	}

}