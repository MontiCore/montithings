// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.ConfigParams
import java.util.Map
import java.util.List;
import montithings.generator.codegen.xtend.util.Comm

class Deploy {
	
	def static generateDeploy(ComponentTypeSymbol comp, String compname, ConfigParams config, Map<String,List<String>> componentPortMap) {
		var helper = new ComponentHelper(comp);
		return '''
		#include "«compname».h"
		#include "«compname»Manager.h"
		#include <chrono>
		#include <thread>

		int main()
		{
			«ComponentHelper.printPackageNamespaceForComponent(comp)»«compname» cmp;
			«IF config.getSplittingMode() != ConfigParams.SplittingMode.OFF»	
			«ComponentHelper.printPackageNamespaceForComponent(comp)»«compname»Manager manager (&cmp);
			manager.initializePorts ();
			«IF comp.isDecomposed»
		    manager.searchSubcomponents ();
			«ENDIF»
			«ENDIF»

			cmp.setUp(«IF ComponentHelper.isTimesync(comp)»TIMESYNC«ELSE»EVENTBASED«ENDIF»);
			cmp.init();
			«IF !ComponentHelper.isTimesync(comp)»
			cmp.start();
			«ENDIF»

			
			std::cout << "Started." << std::endl;
		
			while (true)
        {
          auto end = std::chrono::high_resolution_clock::now() + «ComponentHelper.getExecutionIntervalMethod(comp)»;
          «IF ComponentHelper.isTimesync(comp)»
          cmp.compute();
          «ENDIF»
          do {
            std::this_thread::yield();
            «IF ComponentHelper.isTimesync(comp)»
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

	
	def static generateDeployArduino(ComponentTypeSymbol comp, String compname) {
		return '''
		#include "«compname».h"
		
		«ComponentHelper.printPackageNamespaceForComponent(comp)»«compname» cmp;
		const long interval = «ComponentHelper.getExecutionIntervalInMillis(comp)»;
		unsigned long previousMillis = 0;
		
		void setup() {
		  Serial.begin(9600);
		  cmp.setUp(«IF ComponentHelper.isTimesync(comp)»TIMESYNC«ELSE»EVENTBASED«ENDIF»);
		  cmp.init();
		  «IF !ComponentHelper.isTimesync(comp)»
		  cmp.start();
		  «ENDIF»
		}
		
		void loop() {
		  «IF ComponentHelper.isTimesync(comp)»
		  unsigned long currentMillis = millis();

		  if (currentMillis >= previousMillis + interval) {
		    previousMillis = currentMillis;
		    cmp.compute();
		  }
		  «ENDIF»
		}
		'''
	}
}