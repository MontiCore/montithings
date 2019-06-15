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
		
			while (true)
			{
				auto end = std::chrono::high_resolution_clock::now() + std::chrono::milliseconds(50);
				cmp.compute();
				cmp.update();
				do {
				        std::this_thread::yield();
				    } while (std::chrono::high_resolution_clock::now()  < end);
			}
		
		
			return 0;
		}
		
		'''
	}

}