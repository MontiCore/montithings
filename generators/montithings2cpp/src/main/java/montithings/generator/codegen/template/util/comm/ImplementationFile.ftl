# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.ComponentInstanceSymbol
import arcbasis._symboltable.PortSymbol
import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import montithings._ast.ASTMTComponentType
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.ConfigParams-->

  def static String generateImplementationFile (ComponentTypeSymbol comp, ConfigParams config) {
    return '''
    #include "${comp.getName()}Manager.h"
    #include "messages/PortToSocket.h"
    <#if config.getSplittingMode().toString() == "LOCAL">
    #include "json/json.hpp"
    #include <fstream>
    </#if>

    ${Utils.printNamespaceStart(comp)}

    <#if config.getSplittingMode().toString() == "LOCAL">
 using json = nlohmann::json;
 </#if>

    ${comp.getName()}Manager::${comp.getName()}Manager (${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp, std::string managementPort, std::string communicationPort)
      : comp (comp), managementPort (managementPort), communicationPort (communicationPort)
    {
      comm = new ManagementCommunication ();
      comm->init(managementPort);
      comm->registerMessageProcessor (this);
      portConfigFilePath = "ports/" + comp->getInstanceName () + ".json";
    }

    void
    ${comp.getName()}Manager::process (std::string msg)
    {
      ${printCheckForManagementInstructions(comp, config)}
    }

    void
    ${comp.getName()}Manager::initializePorts ()
    {
      ${printInitializePorts(comp, config)}
    }

    void
    ${comp.getName()}Manager::searchSubcomponents ()
    {
      ${printSearchForSubComps(comp, config)}
    }

    ${Utils.printNamespaceEnd(comp)}
    '''
  }

  def static String printInitializePorts(ComponentTypeSymbol comp, ConfigParams config){
    return '''
    // initialize ports
    ${FOR PortSymbol p: comp.getPorts()}
    <#assign type = ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)>
    std::string ${p.getName()}_uri = "ws://" + comm->getOurIp() + ":" + communicationPort + "/" + comp->getInstanceName () + "/out/${p.getName()}";
    comp->addOutPort${p.getName()?cap_first()}(new WSPort<${type}>(OUTGOING, ${p.getName()}_uri));
    </#list>
    '''
  }

  def static String printCheckForManagementInstructions(ComponentTypeSymbol comp, ConfigParams config){
    return '''
    PortToSocket message(msg);

    ${FOR PortSymbol p: comp.getPorts()}
    <#if !p.isOutgoing()>
    if (message.getLocalPort() == "${p.getName()}")
    {
      // connection information for port ${p.getName()} was received
      std::string ${p.getName()}_uri = "ws://" + message.getIpAndPort() + message.getRemotePort();
      std::cout << "Received connection: " << ${p.getName()}_uri << std::endl;
      comp->addInPort${p.getName()?cap_first()}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)}>(INCOMING, ${p.getName()}_uri));
    }
    </#if>
    </#list>
    '''
  }

  def static String printSearchForSubComps(ComponentTypeSymbol comp, ConfigParams config){
    if (comp.getSubComponents().isEmpty) {
      return '// component has no subcomponents - nothing to do'
    } else {
    return '''
    bool allConnected = 0;
    while (!allConnected)
    {
      std::cout << "Searching for subcomponents\n";
      <#list comp.subComponents as subcomponent>
      <#assign subcomponentSymbol = subcomponent.type.loadedSymbol>
      // ${subcomponentSymbol.getName()} ${subcomponent.getName()}
      ${printSCDetailsHelper(comp, subcomponent)}

        <#if config.getSplittingMode().toString() == "LOCAL">
        std::ifstream i (this->portConfigFilePath);
        json j;
        i >> j;
        std::string ${subcomponent.getName()}_port = j["${subcomponent.getName()}"]["management"].get<std::string> ();
        <#else>
        std::string ${subcomponent.getName()}_port = "1337";
        </#if>

        std::string ${subcomponent.getName()}_ip = comm->getIpOfComponent ("${subcomponent.getName()}");
        

        // tell subcomponent where to connect to
        ${FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()}
        ${FOR ASTPortAccess target : connector.targetList}
          <#if !target.isPresentComponent && subcomponent.getName() == connector.source.component>
            ${FOR PortSymbol p: subcomponentSymbol.ports}
            <#if p.getName() == connector.source.port>
            // set receiver
            std::string communicationPort = j["${subcomponent.getName()}"]["communication"].get<std::string> ();
            std::string ${subcomponent.getName()}_uri = "ws://" + ${subcomponent.getName()}_ip + ":" + communicationPort + "/" + comp->getInstanceName () + ".${subcomponent.getName()}/out/${p.getName()}";
            
            // implements "${connector.source.getQName} -> ${target.getQName}"
            comp->addInPort${target.port?cap_first}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)}>(INCOMING, ${subcomponent.getName()}_uri));
            
            </#if>
            </#list>
          </#if>
          <#-- TODO: What happens if !target.isPresentComponent --><#if target.isPresentComponent && subcomponent.getName() == target.component>
          {
            <#if !connector.source.isPresentComponent>
              PortToSocket message ("${target.port}", comm->getOurIp() + ":" + communicationPort, "/" + comp->getInstanceName () + "/out/${connector.source.port}");
            <#else>
              <#list comp.subComponents as sourceSubcomp>
              <#if sourceSubcomp.getName() == connector.source.component>
              std::string communicationPort = j["${connector.source.component}"]["communication"].get<std::string> ();
              PortToSocket message ("${target.port}", comp->get${connector.source.component?cap_first}IP() + ":" + communicationPort, "/${sourceSubcomp.fullName}/out/${connector.source.port}");
              </#if>
              </#list>
            </#if>
            
            comm->sendManagementMessage (${subcomponent.getName()}_ip, ${subcomponent.getName()}_port, &message);
          }
          </#if>
        </#list>
        </#list>

        comp->set${subcomponent.getName()?cap_first}IP(${subcomponent.getName()}_ip);
        std::cout << "Found ${subcomponentSymbol.getName()} ${subcomponent.getName()}\n";

        continue;
      }
      </#list>

      // continue if all components are connected
      allConnected = 
      <#list comp.subComponents as subcomponent >
 comp->get${subcomponent.getName()?cap_first}IP().length() != 0<#sep>&&</#sep>
 </#list>;
      if (!allConnected) {
        // circuit breaker
        std::this_thread::sleep_for(std::chrono::milliseconds(100));
      }
      std::cout << "Found all subcomponents." << std::endl;
    }
    '''
  }
  }

  def static String printSCDetailsHelper(ComponentTypeSymbol comp, ComponentInstanceSymbol subcomponent){
    <#assign s = "if (comp->get" + subcomponent.getName()?cap_first + "IP().length() == 0">
    for (ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()){
    for (ASTPortAccess target : connector.targetList){
      // TODO: What happens when !target.isPresentComponent
      if (target.isPresentComponent && subcomponent.getName() == target.component){
      if (connector.source.isPresentComponent){
        s += " && comp->get" + connector.source.component?cap_first + "IP().length() != 0"
      }
      }
    }
    }
    s += ") {"
    return s
  }

  def static String printInitURIs(ComponentTypeSymbol comp){
    return '''
    <#list comp.ports as PortSymbol p >
 std::string ${p.getName()}_uri;
 </#list>
    <#list comp.subComponents as subcomponent >
 std::string ${subcomponent.getName()}_uri;
 </#list>
    ${FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()}
    ${FOR ASTPortAccess target : connector.targetList}
    <#list comp.subComponents as subcomponent>
      <#assign subcomponentSymbol = subcomponent.type.loadedSymbol>
      <#if !connector.source.isPresentComponent && subcomponent.getName() == target.component>
      ${FOR PortSymbol p : subcomponentSymbol.ports}
      <#if p.getName() == target.port>
 std::string to${subcomponent.getName()?cap_first}_${p.getName()}_uri;
 </#if>
      </#list>
      </#if>
    </#list>
    </#list>
    </#list>
    std::string comm_in_uri;
    std::string comm_out_uri;
    '''
  }