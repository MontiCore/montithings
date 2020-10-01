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
    #include "${comp.name}Manager.h"
    #include "messages/PortToSocket.h"
    <#if config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL>
    #include "json/json.hpp"
    #include <fstream>
    </#if>

    ${Utils.printNamespaceStart(comp)}

    <#if config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL>
 using json = nlohmann::json;
 </#if>

    ${comp.name}Manager::${comp.name}Manager (${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name} *comp, std::string managementPort, std::string communicationPort)
      : comp (comp), managementPort (managementPort), communicationPort (communicationPort)
    {
      comm = new ManagementCommunication ();
      comm->init(managementPort);
      comm->registerMessageProcessor (this);
      portConfigFilePath = "ports/" + comp->getInstanceName () + ".json";
    }

    void
    ${comp.name}Manager::process (std::string msg)
    {
      ${printCheckForManagementInstructions(comp, config)}
    }

    void
    ${comp.name}Manager::initializePorts ()
    {
      ${printInitializePorts(comp, config)}
    }

    void
    ${comp.name}Manager::searchSubcomponents ()
    {
      ${printSearchForSubComps(comp, config)}
    }

    ${Utils.printNamespaceEnd(comp)}
    '''
  }

  def static String printInitializePorts(ComponentTypeSymbol comp, ConfigParams config){
    return '''
    // initialize ports
    ${FOR PortSymbol p: comp.ports}
    <#assign type = ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)>
    std::string ${p.name}_uri = "ws://" + comm->getOurIp() + ":" + communicationPort + "/" + comp->getInstanceName () + "/out/${p.name}";
    comp->addOutPort${p.name.toFirstUpper()}(new WSPort<${type}>(OUTGOING, ${p.name}_uri));
    </#list>
    '''
  }

  def static String printCheckForManagementInstructions(ComponentTypeSymbol comp, ConfigParams config){
    return '''
    PortToSocket message(msg);

    ${FOR PortSymbol p: comp.ports}
    <#if !p.isOutgoing()>
    if (message.getLocalPort() == "${p.name}")
    {
      // connection information for port ${p.name} was received
      std::string ${p.name}_uri = "ws://" + message.getIpAndPort() + message.getRemotePort();
      std::cout << "Received connection: " << ${p.name}_uri << std::endl;
      comp->addInPort${p.name.toFirstUpper()}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)}>(INCOMING, ${p.name}_uri));
    }
    </#if>
    </#list>
    '''
  }

  def static String printSearchForSubComps(ComponentTypeSymbol comp, ConfigParams config){
    if (comp.subComponents.isEmpty) {
      return '// component has no subcomponents - nothing to do'
    } else {
    return '''
    bool allConnected = 0;
    while (!allConnected)
    {
      std::cout << "Searching for subcomponents\n";
      <#list comp.subComponents as subcomponent>
      <#assign subcomponentSymbol = subcomponent.type.loadedSymbol>
      // ${subcomponentSymbol.name} ${subcomponent.name}
      ${printSCDetailsHelper(comp, subcomponent)}

        <#if config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL>
        std::ifstream i (this->portConfigFilePath);
        json j;
        i >> j;
        std::string ${subcomponent.name}_port = j["${subcomponent.name}"]["management"].get<std::string> ();
        ${ELSE}
        std::string ${subcomponent.name}_port = "1337";
        </#if>

        std::string ${subcomponent.name}_ip = comm->getIpOfComponent ("${subcomponent.name}");
        

        // tell subcomponent where to connect to
        ${FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()}
        ${FOR ASTPortAccess target : connector.targetList}
          <#if !target.isPresentComponent && subcomponent.name == connector.source.component>
            ${FOR PortSymbol p: subcomponentSymbol.ports}
            <#if p.name == connector.source.port>
            // set receiver
            std::string communicationPort = j["${subcomponent.name}"]["communication"].get<std::string> ();
            std::string ${subcomponent.name}_uri = "ws://" + ${subcomponent.name}_ip + ":" + communicationPort + "/" + comp->getInstanceName () + ".${subcomponent.name}/out/${p.name}";
            
            // implements "${connector.source.getQName} -> ${target.getQName}"
            comp->addInPort${target.port.toFirstUpper}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.component.get, p, config)}>(INCOMING, ${subcomponent.name}_uri));
            
            </#if>
            </#list>
          </#if>
          <#-- TODO: What happens if !target.isPresentComponent --><#if target.isPresentComponent && subcomponent.name == target.component>
          {
            <#if !connector.source.isPresentComponent>
              PortToSocket message ("${target.port}", comm->getOurIp() + ":" + communicationPort, "/" + comp->getInstanceName () + "/out/${connector.source.port}");
            ${ELSE}
              <#list comp.subComponents as sourceSubcomp>
              <#if sourceSubcomp.name == connector.source.component>
              std::string communicationPort = j["${connector.source.component}"]["communication"].get<std::string> ();
              PortToSocket message ("${target.port}", comp->get${connector.source.component.toFirstUpper}IP() + ":" + communicationPort, "/${sourceSubcomp.fullName}/out/${connector.source.port}");
              </#if>
              </#list>
            </#if>
            
            comm->sendManagementMessage (${subcomponent.name}_ip, ${subcomponent.name}_port, &message);
          }
          </#if>
        </#list>
        </#list>

        comp->set${subcomponent.name.toFirstUpper}IP(${subcomponent.name}_ip);
        std::cout << "Found ${subcomponentSymbol.name} ${subcomponent.name}\n";

        continue;
      }
      </#list>

      // continue if all components are connected
      allConnected = 
      <#list comp.subComponents as subcomponent >
 comp->get${subcomponent.name.toFirstUpper}IP().length() != 0<#sep>&&</#sep>
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
    <#assign s = "if (comp->get" + subcomponent.name.toFirstUpper + "IP().length() == 0">
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
    return '''
    <#list comp.ports as PortSymbol p >
 std::string ${p.name}_uri;
 </#list>
    <#list comp.subComponents as subcomponent >
 std::string ${subcomponent.name}_uri;
 </#list>
    ${FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()}
    ${FOR ASTPortAccess target : connector.targetList}
    <#list comp.subComponents as subcomponent>
      <#assign subcomponentSymbol = subcomponent.type.loadedSymbol>
      <#if !connector.source.isPresentComponent && subcomponent.name == target.component>
      ${FOR PortSymbol p : subcomponentSymbol.ports}
      <#if p.name == target.port>
 std::string to${subcomponent.name.toFirstUpper}_${p.name}_uri;
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