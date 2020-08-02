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

    def static String printInitializePorts(ComponentTypeSymbol comp, Map<String,List<String>> componentPortMap, ConfigParams config){
        var helper = new ComponentHelper(comp);
        return '''
        // initialize ports
        «FOR PortSymbol p: comp.ports»
        «var type = ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»
        «IF p.outgoing»
        «p.name»_uri = "ws://" + this_ip + ":«componentPortMap.get(comp.name).get(1)»/«comp.name»/out/«p.name»";
        cmp->addOutPort«p.name.toFirstUpper()»(new WSPort<«type»>(OUT, «p.name»_uri.c_str()));
        «ELSE»
        «p.name»_uri = "ws://" + this_ip + ":«componentPortMap.get(comp.name).get(1)»/«comp.name»/out/«p.name»";
        cmp->addOutPort«p.name.toFirstUpper()»(new WSPort<«type»>(OUT, «p.name»_uri.c_str()));
        «ENDIF»
        «ENDFOR»

«««        «FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
«««		«FOR ASTPortAccess target : connector.targetList»
«««        «FOR subcomponent : comp.subComponents»
«««            «var subcomponentSymbol = subcomponent.type.loadedSymbol»
«««            «IF !connector.source.isPresentComponent && subcomponent.name == target.component»
«««                «FOR PortSymbol p: subcomponentSymbol.ports»
«««                «IF p.name == target.port»
«««                to«subcomponent.name.toFirstUpper»_«p.name»_uri = "ws://" + this_ip + ":«componentPortMap.get(comp.name).get(1)»/«comp.name»/out/to«subcomponent.name.toFirstUpper»/«p.name»";
«««                cmp->setTo«subcomponent.name.toFirstUpper»_«p.name»(new WSPort<«ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»>(OUT, to«subcomponent.name.toFirstUpper»_«p.name»_uri.c_str()));
«««                «ENDIF»
«««                «ENDFOR»
«««            «ENDIF»
«««        «ENDFOR»
«««        «ENDFOR»
«««        «ENDFOR»

        // communication in_port
        comm_in_uri = "ws://" + this_ip + ":«componentPortMap.get(comp.name).get(0)»";
        managementIn = new WSPort<std::string>(IN, comm_in_uri.c_str(), false);
        '''
    }

    def static String printCheckForManagementInstructions(ComponentTypeSymbol comp, ConfigParams config){
        var helper = new ComponentHelper(comp);
        return '''
        // declare communication strings
        std::string local_port = "";
        std::string remote_port = "";
        std::string ip = "";
        std::string m = "";

        // wait for connection request: 'local_port=X,ip=localhost:8080,remote_port=/a/b/c'
        while(true){
            std::cout << "Waiting for connection\n";
            std::this_thread::sleep_for(std::chrono::milliseconds(1000));

            tl::optional<std::string> msg = managementIn->getCurrentValue(uuid);
            if(msg){ m = msg.value(); }
            if(m.length() > 0){
                local_port = m.substr(11, m.find(",ip=") - 11);
                ip = m.substr(m.find(",ip=") + 4, m.find(",remote_port=") - m.find(",ip=") - 4);
                remote_port = m.substr(m.find(",remote_port=") + 13);
            }

            «FOR PortSymbol p: comp.ports»
            «IF !p.isOutgoing()»
            if(local_port == "«p.name»"){
              // connection information for port «p.name» was received
              std::cout << "Received connection from " + ip + remote_port + "\n";
              «p.name»_uri = "ws://" + ip + remote_port;
              cmp->addInPort«p.name.toFirstUpper()»(new WSPort<«ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»>(IN, «p.name»_uri.c_str()));
              «FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
              «FOR ASTPortAccess target : connector.targetList»
              «FOR subcomponent : comp.subComponents»
                «var subcomponentSymbol = subcomponent.type.loadedSymbol»
                  «IF p.name == connector.source.port»
                    // implements "«connector.source.getQName» -> «target.getQName»"
                    cmp->getTo«subcomponent.name.toFirstUpper»_«target.port»()->setDataProvidingPort(cmp->getPort«p.name.toFirstUpper»());
                  «ENDIF»
              «ENDFOR»
              «ENDFOR»
              «ENDFOR»
            }
            «ENDIF»
            «ENDFOR»

            «FOR PortSymbol p: comp.ports»
            «IF !p.isOutgoing()»
            if(!cmp->getPort«p.name.toFirstUpper»()){ continue; }
            «ENDIF»
            «ENDFOR»
            std::this_thread::sleep_for(std::chrono::milliseconds(1000));
            break;
        }
        '''
    }

    def static String printSearchForSubComps(ComponentTypeSymbol comp, Map<String,List<String>> componentPortMap, ConfigParams config){
        var helper = new ComponentHelper(comp);
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

                httplib::Client cli(manager_ip, 8080);

                std::string body;

                //TODO: add application name to route
                auto res = cli.Get("/api/v1/resources/distribution/latest?component=«subcomponent.name»",
                  [&](const char *data, size_t data_length) {
                    body.append(data, data_length);
                    return true;
                  });

                //TODO: Also get port from api
                std::string «subcomponent.name»_ip = "";
                std::string «subcomponent.name»_port = "";
                if (!res || (res && res->status != 200)) {
                    «subcomponent.name»_ip = "127.0.0.1";
                    «subcomponent.name»_port = "«componentPortMap.get(subcomponentSymbol.name).get(0)»";
                } else {
                    «subcomponent.name»_ip = body.c_str();
                    «subcomponent.name»_port = "1337";
                }

                if («subcomponent.name»_ip.length() != 0){
                // tell subcomponent where to connect to
                comm_out_uri = "ws://" + «subcomponent.name»_ip + ":" + «subcomponent.name»_port;
                managementOut = new WSPort<std::string>(OUT, comm_out_uri.c_str(), false);
                «FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
                «FOR ASTPortAccess target : connector.targetList»
                    «IF !target.isPresentComponent && subcomponent.name == connector.source.component»
                        «FOR PortSymbol p: subcomponentSymbol.ports»
                        «IF p.name == connector.source.port»
                        // set receiver
                        «subcomponent.name»_uri = "ws://" + «subcomponent.name»_ip + ":«componentPortMap.get(subcomponentSymbol.name).get(1)»/«subcomponent.name.toFirstUpper()»/out/«p.name»";
                        
                        // implements "«connector.source.getQName» -> «target.getQName»"
                        cmp->addInPort«target.port.toFirstUpper»(new WSPort<«ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)»>(IN, «subcomponent.name»_uri.c_str()));
                        
                        «ENDIF»
                        «ENDFOR»
                    «ENDIF»
                    ««« TODO: What happens if !target.isPresentComponent
                    «IF target.isPresentComponent && subcomponent.name == target.component»
                        std::this_thread::sleep_for(std::chrono::milliseconds(1000));
                        «IF !connector.source.isPresentComponent»
                            managementOut->setNextValue("local_port=«target.port»,ip=" + this_ip + ":«componentPortMap.get(comp.name).get(1)»,remote_port=/«comp.name»/out/to«subcomponent.name.toFirstUpper»/«target.port»");
                        «ELSE»
                            «FOR source_sc : comp.subComponents»
                            «var source_sc_symbol = source_sc.type.loadedSymbol»
                            «IF source_sc.name == connector.source.component»
                            managementOut->setNextValue("local_port=«target.port»,ip=" + cmp->get«connector.source.component.toFirstUpper»IP() + ":«componentPortMap.get(source_sc_symbol.name).get(1)»,remote_port=/«source_sc_symbol.name»/out/«connector.source.port»");
                            «ENDIF»
                            «ENDFOR»
                        «ENDIF»
                    «ENDIF»
                «ENDFOR»
                «ENDFOR»
                cmp->set«subcomponent.name.toFirstUpper»IP(«subcomponent.name»_ip);
                std::cout << "Found «subcomponentSymbol.name»\n";
                // kill communication port
                std::this_thread::sleep_for(std::chrono::milliseconds(1000));
                managementOut->killThread();
                managementOut = nullptr;
                }
                continue;
            }
            «ENDFOR»

            // continue if all components are connected
            allConnected = 1;
        }
        '''
    }

    def static String printReadManagerIP(){
        return '''
        std::ifstream i("/app/bin/properties.json");
        std::string manager_ip = "";
        // TODO: Is a fallback ip necessary/ does it make sense?
        if(i.fail()){
            manager_ip = "127.0.0.1";
        } else {
            json j;
            i >> j;
            manager_ip = j["managerIp"].get<std::string>();
        }
        return manager_ip;
        '''
    }

    def static String printReadComponentIP(){
        return '''
        std::ifstream i("/app/bin/properties.json");
        std::string this_ip = "";
        // TODO: Is a fallback ip necessary/ does it make sense?
        if(i.fail()){
            this_ip = "127.0.0.1";
        } else {
            json j;
            i >> j;
            this_ip = j["componentIp"].get<std::string>();
        }
        return this_ip;
        '''
    }

    def static String printSCDetailsHelper(ComponentTypeSymbol comp, ComponentInstanceSymbol subcomponent){
        var helper = new ComponentHelper(comp);
        var s = "if (cmp->get" + subcomponent.name.toFirstUpper + "IP().length() == 0"
        for (ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()){
        for (ASTPortAccess target : connector.targetList){
            // TODO: What happens when !target.isPresentComponent
          if (target.isPresentComponent && subcomponent.name == target.component){
            if (connector.source.isPresentComponent){
              s += " && cmp->get" + connector.source.component.toFirstUpper + "IP().length() != 0"
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