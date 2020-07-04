// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.helper.ComponentHelper

class Deploy {
	
	def static generateDeploy(ComponentTypeSymbol comp, String compname) {
		return '''
		#include "«compname».h"
		#include <chrono>
		#include <thread>
		
		int main()
		{
			«ComponentHelper.printPackageNamespaceForComponent(comp)»«compname» cmp;
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
	
	def static generateDeployArduino(ComponentTypeSymbol comp, String compname) {
		return '''
		#include "«compname».h"
		
		«ComponentHelper.printPackageNamespaceForComponent(comp)»«compname» cmp;
		const long interval = «ComponentHelper.getExecutionIntervalInMillis(comp)»;
		unsigned long previousMillis = 0;
		
		void setup() {
		  Serial.begin(9600);
		  cmp.setUp(«IF comp.getStereotype().containsKey("timesync")»TIMESYNC«ELSE»EVENTBASED«ENDIF»);
		  cmp.init();
		  «IF !comp.getStereotype().containsKey("timesync")»
		  cmp.start();
		  «ENDIF»
		}
		
		void loop() {
		  «IF comp.getStereotype().containsKey("timesync")»
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