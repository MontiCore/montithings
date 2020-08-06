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

    def static String generateHeader (ComponentTypeSymbol comp) {
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

        public:
        «comp.name»Manager («ComponentHelper.printPackageNamespaceForComponent(comp)»«comp.name» *comp);

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

    def static String generateImplementationFile (ComponentTypeSymbol comp, Map<String,List<String>> componentPortMap, ConfigParams config) {
        return '''
        #include "«comp.name»Manager.h"
        #include "messages/PortToSocket.h"

        «Utils.printNamespaceStart(comp)»

        «comp.name»Manager::«comp.name»Manager («ComponentHelper.printPackageNamespaceForComponent(comp)»«comp.name» *comp)
            : comp (comp)
        {
            comm = new ManagementCommunication ();
			comm->init("«componentPortMap.get(comp.name).get(0)»");
            comm->registerMessageProcessor (this);
        }

        void
        «comp.name»Manager::process (std::string msg)
        {
            «printCheckForManagementInstructions(comp, config)»
        }

        void
        «comp.name»Manager::initializePorts ()
        {
            «printInitializePorts(comp, componentPortMap, config)»
        }

        void
        «comp.name»Manager::searchSubcomponents ()
        {
            «printSearchForSubComps(comp, componentPortMap, config)»
        }

        «Utils.printNamespaceEnd(comp)»
        '''
    }

    def static String printInitializePorts(ComponentTypeSymbol comp, Map<String,List<String>> componentPortMap, ConfigParams config){
        var helper = new ComponentHelper(comp);
        return '''
        // initialize ports
        «FOR PortSymbol p: comp.ports»
        «var type = ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»
        «IF p.outgoing»
        std::string «p.name»_uri = "ws://" + comm->getOurIp() + ":«componentPortMap.get(comp.name).get(1)»/" + comp->getInstanceName () + "/out/«p.name»";
        comp->addOutPort«p.name.toFirstUpper()»(new WSPort<«type»>(OUT, «p.name»_uri));
        «ELSE»
        std::string «p.name»_uri = "ws://" + comm->getOurIp() + ":«componentPortMap.get(comp.name).get(1)»/" + comp->getInstanceName () + "/out/«p.name»";
        comp->addOutPort«p.name.toFirstUpper()»(new WSPort<«type»>(OUT, «p.name»_uri));
        «ENDIF»
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

    def static String printSearchForSubComps(ComponentTypeSymbol comp, Map<String,List<String>> componentPortMap, ConfigParams config){
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
            // «subcomponentSymbol.name»
            «printSCDetailsHelper(comp, subcomponent)»

                std::string «subcomponent.name»_ip = comm->getIpOfComponent ("«subcomponent.name»");
                std::string «subcomponent.name»_port = «subcomponent.name»_ip == "127.0.0.1" ? "«componentPortMap.get(subcomponentSymbol.name).get(0)»" : "1337";

                // tell subcomponent where to connect to
                «FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
                «FOR ASTPortAccess target : connector.targetList»
                    «IF !target.isPresentComponent && subcomponent.name == connector.source.component»
                        «FOR PortSymbol p: subcomponentSymbol.ports»
                        «IF p.name == connector.source.port»
                        // set receiver
                        std::string «subcomponent.name»_uri = "ws://" + «subcomponent.name»_ip + ":«componentPortMap.get(subcomponentSymbol.name).get(1)»/" + comp->getInstanceName () + ".«subcomponent.name»/out/«p.name»";
                        
                        // implements "«connector.source.getQName» -> «target.getQName»"
                        comp->addInPort«target.port.toFirstUpper»(new WSPort<«ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»>(IN, «subcomponent.name»_uri));
                        
                        «ENDIF»
                        «ENDFOR»
                    «ENDIF»
                    ««« TODO: What happens if !target.isPresentComponent
                    «IF target.isPresentComponent && subcomponent.name == target.component»
                    {
                        «IF !connector.source.isPresentComponent»
                            PortToSocket message ("«target.port»", comm->getOurIp() + ":«componentPortMap.get(comp.name).get(1)»", "/" + comp->getInstanceName () + "/out/«connector.source.port»");
                        «ELSE»
                            «FOR sourceSubcomp : comp.subComponents»
                            «var sourceSubcompSymb = sourceSubcomp.type.loadedSymbol»
                            «IF sourceSubcomp.name == connector.source.component»
                            PortToSocket message ("«target.port»", comp->get«connector.source.component.toFirstUpper»IP() + ":«componentPortMap.get(sourceSubcompSymb.name).get(1)»", "/«sourceSubcomp.fullName»/out/«connector.source.port»");
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

}