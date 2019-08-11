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
			cmp.start();
		
			while (true)
			{
				auto end = std::chrono::high_resolution_clock::now() + std::chrono::milliseconds(50);
				//TODO: 
				do {
				        std::this_thread::yield();
                        std::this_thread::sleep_for(std::chrono::milliseconds(1000));
				        
				    } while (std::chrono::high_resolution_clock::now()  < end);
			}
		
		
			return 0;
		}
		
		'''
	}

}