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
		#include <chrono>
		#include <thread>
		#include "cpp-httplib/httplib.h"
		#include "json/json.hpp"
		#include "WSPort.h"

		using json = nlohmann::json;

		// helper functions
		std::string readManagerIP();
		std::string readComponentIP();
		void initializePorts(std::string this_ip, «ComponentHelper.printPackageNamespaceForComponent(comp)»«compname»* cmp);
		void waitForSuperComponent(«ComponentHelper.printPackageNamespaceForComponent(comp)»«compname»* cmp);
		void searchForSubComps(std::string this_ip, std::string manager_ip, «ComponentHelper.printPackageNamespaceForComponent(comp)»«compname»* cmp);

		// declare URIs globally to ensure the ports work correctly
		«Comm.printInitURIs(comp)»

		int main()
		{
		    «ComponentHelper.printPackageNamespaceForComponent(comp)»«compname» cmp;

		    // read IoT Manager IP from config file
		    std::string manager_ip = readManagerIP();

		    // read own IP from config file
		    std::string this_ip = readComponentIP();

		    // initialization
		    initializePorts(this_ip, &cmp);

		    // wait for super component to connect
		    waitForSuperComponent(&cmp);

		    // search for subcomponents
		    searchForSubComps(this_ip, manager_ip, &cmp);
		    
		    std::cout << "Found all subcomponents." << std::endl;

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

		// read IoT Manager IP from config file
		std::string readManagerIP(){
		    «Comm.printReadManagerIP()»
		}

		// get own IP from IoT Manager
		std::string readComponentIP(){
		    «Comm.printReadComponentIP()»
		}

		// initialize ports
		void initializePorts(std::string this_ip, «ComponentHelper.printPackageNamespaceForComponent(comp)»«compname»* cmp){
            «Comm.printInitDetails(comp, componentPortMap, config)»
		}

		// wait for super component to connect
		void waitForSuperComponent(«ComponentHelper.printPackageNamespaceForComponent(comp)»«compname»* cmp){
		    «Comm.printSuperConnectDetails(comp, config)»
		}
		// search for subcomponents
		void searchForSubComps(std::string this_ip, std::string manager_ip, «ComponentHelper.printPackageNamespaceForComponent(comp)»«compname»* cmp){
            «Comm.printSubConnectDetails(comp, componentPortMap, config)»
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