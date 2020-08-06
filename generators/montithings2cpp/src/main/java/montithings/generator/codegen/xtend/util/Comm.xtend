package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.ComponentInstanceSymbol
import arcbasis._symboltable.PortSymbol
import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import montithings._ast.ASTMTComponentType
import java.util.List;
import java.util.Map
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.ConfigParams

class Comm {

	def static String generateHeader (ComponentTypeSymbol comp, ConfigParams config) {
		return '''
		#pragma once
		#include "«comp.name».h"
		#include "ManagementCommunication.h"
		#include "ManagementMessageProcessor.h"

		«Utils.printNamespaceStart(comp)»

		class «comp.name»Manager : public ManagementMessageProcessor
		{
		protected:
		montithings::hierarchy::«comp.name»* comp;
		ManagementCommunication* comm;
		std::string managementPort;
		std::string communicationPort;
		«IF config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL»
		std::string portConfigFilePath;
		«ENDIF»

		public:
		«comp.name»Manager («ComponentHelper.printPackageNamespaceForComponent(comp)»«comp.name» *comp, std::string managementPort, std::string communicationPort);

		/* 
		 * Process management instructions from the enclosing component
		 * Those are mostly connectors to other components
		 */
		void process (std::string msg) override;

		/*
		 * Initially create ports of this component
		 */
		void initializePorts ();

		/*
		 * Search for subcomponents
		 * Tell subcomponents to which ports of other components they should connect
		 */
		void searchSubcomponents ();
		};

		«Utils.printNamespaceEnd(comp)»
		'''
	}

	def static String generateImplementationFile (ComponentTypeSymbol comp, ConfigParams config) {
		return '''
		#include "«comp.name»Manager.h"
		#include "messages/PortToSocket.h"
		«IF config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL»
		#include "json/json.hpp"
		#include <fstream>
		«ENDIF»

		«Utils.printNamespaceStart(comp)»

		«IF config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL»
		using json = nlohmann::json;
		«ENDIF»

		«comp.name»Manager::«comp.name»Manager («ComponentHelper.printPackageNamespaceForComponent(comp)»«comp.name» *comp, std::string managementPort, std::string communicationPort)
			: comp (comp), managementPort (managementPort), communicationPort (communicationPort)
		{
			comm = new ManagementCommunication ();
			comm->init(managementPort);
			comm->registerMessageProcessor (this);
			portConfigFilePath = "ports/" + comp->getInstanceName () + ".json";
		}

		void
		«comp.name»Manager::process (std::string msg)
		{
			«printCheckForManagementInstructions(comp, config)»
		}

		void
		«comp.name»Manager::initializePorts ()
		{
			«printInitializePorts(comp, config)»
		}

		void
		«comp.name»Manager::searchSubcomponents ()
		{
			«printSearchForSubComps(comp, config)»
		}

		«Utils.printNamespaceEnd(comp)»
		'''
	}

	def static String printInitializePorts(ComponentTypeSymbol comp, ConfigParams config){
		var helper = new ComponentHelper(comp);
		return '''
		// initialize ports
		«FOR PortSymbol p: comp.ports»
		«var type = ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»
		std::string «p.name»_uri = "ws://" + comm->getOurIp() + ":" + communicationPort + "/" + comp->getInstanceName () + "/out/«p.name»";
		comp->addOutPort«p.name.toFirstUpper()»(new WSPort<«type»>(OUT, «p.name»_uri));
		«ENDFOR»
		'''
	}

	def static String printCheckForManagementInstructions(ComponentTypeSymbol comp, ConfigParams config){
		var helper = new ComponentHelper(comp);
		return '''
		PortToSocket message(msg);

		«FOR PortSymbol p: comp.ports»
		«IF !p.isOutgoing()»
		if (message.getLocalPort() == "«p.name»")
		{
			// connection information for port «p.name» was received
			std::string «p.name»_uri = "ws://" + message.getIpAndPort() + message.getRemotePort();
			std::cout << "Received connection: " << «p.name»_uri << std::endl;
			comp->addInPort«p.name.toFirstUpper()»(new WSPort<«ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»>(IN, «p.name»_uri));
		}
		«ENDIF»
		«ENDFOR»
		'''
	}

	def static String printSearchForSubComps(ComponentTypeSymbol comp, ConfigParams config){
		var helper = new ComponentHelper(comp);
		if (comp.subComponents.isEmpty) {
			return '// component has no subcomponents - nothing to do'
		} else {
		return '''
		bool allConnected = 0;
		while (!allConnected)
		{
			std::cout << "Searching for subcomponents\n";
			std::this_thread::sleep_for(std::chrono::milliseconds(1000));
			«FOR subcomponent : comp.subComponents»
			«var subcomponentSymbol = subcomponent.type.loadedSymbol»
			// «subcomponentSymbol.name» «subcomponent.name»
			«printSCDetailsHelper(comp, subcomponent)»

				«IF config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL»
				std::ifstream i (this->portConfigFilePath);
				json j;
				i >> j;
				std::string «subcomponent.name»_port = j["«subcomponent.name»"]["management"].get<std::string> ();
				«ELSE»
				std::string «subcomponent.name»_port = "1337";
				«ENDIF»

				std::string «subcomponent.name»_ip = comm->getIpOfComponent ("«subcomponent.name»");
				

				// tell subcomponent where to connect to
				«FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
				«FOR ASTPortAccess target : connector.targetList»
					«IF !target.isPresentComponent && subcomponent.name == connector.source.component»
						«FOR PortSymbol p: subcomponentSymbol.ports»
						«IF p.name == connector.source.port»
						// set receiver
						std::string communicationPort = j["«subcomponent.name»"]["communication"].get<std::string> ();
						std::string «subcomponent.name»_uri = "ws://" + «subcomponent.name»_ip + ":" + communicationPort + "/" + comp->getInstanceName () + ".«subcomponent.name»/out/«p.name»";
						
						// implements "«connector.source.getQName» -> «target.getQName»"
						comp->addInPort«target.port.toFirstUpper»(new WSPort<«ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»>(IN, «subcomponent.name»_uri));
						
						«ENDIF»
						«ENDFOR»
					«ENDIF»
					««« TODO: What happens if !target.isPresentComponent
					«IF target.isPresentComponent && subcomponent.name == target.component»
					{
						«IF !connector.source.isPresentComponent»
							PortToSocket message ("«target.port»", comm->getOurIp() + ":" + communicationPort, "/" + comp->getInstanceName () + "/out/«connector.source.port»");
						«ELSE»
							«FOR sourceSubcomp : comp.subComponents»
							«var sourceSubcompSymb = sourceSubcomp.type.loadedSymbol»
							«IF sourceSubcomp.name == connector.source.component»
							std::string communicationPort = j["«connector.source.component»"]["communication"].get<std::string> ();
							PortToSocket message ("«target.port»", comp->get«connector.source.component.toFirstUpper»IP() + ":" + communicationPort, "/«sourceSubcomp.fullName»/out/«connector.source.port»");
							«ENDIF»
							«ENDFOR»
						«ENDIF»
						
						comm->sendManagementMessage («subcomponent.name»_ip, «subcomponent.name»_port, &message);
					}
					«ENDIF»
				«ENDFOR»
				«ENDFOR»

				comp->set«subcomponent.name.toFirstUpper»IP(«subcomponent.name»_ip);
				std::cout << "Found «subcomponentSymbol.name» «subcomponent.name»\n";

				continue;
			}
			«ENDFOR»

			// continue if all components are connected
			allConnected = 1;
			std::cout << "Found all subcomponents." << std::endl;
		}
		'''
	}
	}

	def static String printSCDetailsHelper(ComponentTypeSymbol comp, ComponentInstanceSymbol subcomponent){
		var helper = new ComponentHelper(comp);
		var s = "if (comp->get" + subcomponent.name.toFirstUpper + "IP().length() == 0"
		for (ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()){
		for (ASTPortAccess target : connector.targetList){
			// TODO: What happens when !target.isPresentComponent
		  if (target.isPresentComponent && subcomponent.name == target.component){
			if (connector.source.isPresentComponent){
			  s += " && comp->get" + connector.source.component.toFirstUpper + "IP().length() != 0"
			}
		  }
		}
		}
		s += ") {"
		return s
	}

	def static String printInitURIs(ComponentTypeSymbol comp){
		var helper = new ComponentHelper(comp);
		return '''
		«FOR PortSymbol p : comp.ports»
		std::string «p.name»_uri;
		«ENDFOR»
		«FOR subcomponent : comp.subComponents»
		«var subcomponentSymbol = subcomponent.type.loadedSymbol»
		std::string «subcomponent.name»_uri;
		«ENDFOR»
		«FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
		«FOR ASTPortAccess target : connector.targetList»
		«FOR subcomponent : comp.subComponents»
			«var subcomponentSymbol = subcomponent.type.loadedSymbol»
		  «IF !connector.source.isPresentComponent && subcomponent.name == target.component»
			«FOR PortSymbol p : subcomponentSymbol.ports»
			«IF p.name == target.port»
			  std::string to«subcomponent.name.toFirstUpper»_«p.name»_uri;
			«ENDIF»
			«ENDFOR»
		  «ENDIF»
		«ENDFOR»
		«ENDFOR»
		«ENDFOR»
		std::string comm_in_uri;
		std::string comm_out_uri;
		'''
	}

	def static printPortJson(ComponentTypeSymbol comp, ConfigParams config) {
		printPortJson(comp, config, comp.fullName)
	}

	def static printPortJson(ComponentTypeSymbol comp, ConfigParams config, String prefix) {
		return '''
		{
		«FOR subcomp : comp.subComponents SEPARATOR ","»
			"«subcomp.name»": {
				"management": "«config.componentPortMap.getManagementPort(prefix + "." + subcomp.name)»",
				"communication": "«config.componentPortMap.getCommunicationPort(prefix + "." + subcomp.name)»"
			}
		«ENDFOR»
		}
		'''
	}

}