// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import montiarc._symboltable.ComponentSymbol
import montithings.generator.helper.ComponentHelper

class Deploy {
	
	def static generateDeploy(ComponentSymbol comp, String compname) {
		return '''
		#include "«compname».h"
		#include <chrono>
		#include <thread>
		
		int main()
		{
			«compname» cmp;
			cmp.setUp(«IF comp.getStereotype().containsKey("timesync")»TIMESYNC«ELSE»EVENTBASED«ENDIF»);
			cmp.init();
			«IF !comp.getStereotype().containsKey("timesync")»
			cmp.start();
			«ENDIF»
		
			while (true)
			{
				auto end = std::chrono::high_resolution_clock::now() + «ComponentHelper.getExecutionIntervalMethod(comp)»;
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