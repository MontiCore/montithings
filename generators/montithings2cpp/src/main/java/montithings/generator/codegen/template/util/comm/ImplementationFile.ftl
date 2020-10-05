<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${comp.getName()}Manager.h"
#include "messages/PortToSocket.h"
<#if config.getSplittingMode().toString() == "LOCAL">
  #include "json/json.hpp"
  #include ${"<fstream>"}
</#if>

${Utils.printNamespaceStart(comp)}

<#if config.getSplittingMode().toString() == "LOCAL">
  using json = nlohmann::json;
</#if>

${comp.getName()}Manager::${comp.getName()}Manager
(${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp,
std::string managementPort, std::string communicationPort)
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
  <@checkForManagementInstructions comp config/>
}

void
${comp.getName()}Manager::initializePorts ()
{
  <@initializePorts comp config />
}

void
${comp.getName()}Manager::searchSubcomponents ()
{
  <@searchForSubComps comp config />
}

${Utils.printNamespaceEnd(comp)}

<#macro initializePorts comp config>
    // initialize ports
    <#list comp.getPorts() as p>
        <#assign type = ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)>
        std::string ${p.getName()}_uri = "ws://" + comm->getOurIp() + ":" + communicationPort + "/" + comp->getInstanceName () + "/out/${p.getName()}";
        comp->addOutPort${p.getName()?cap_first}(new WSPort<${type}>(OUTGOING, ${p.getName()}_uri));
    </#list>
</#macro>

<#macro checkForManagementInstructions comp config>
    PortToSocket message(msg);

    <#list comp.getPorts() as p>
        <#if !p.isOutgoing()>
          if (message.getLocalPort() == "${p.getName()}")
          {
          // connection information for port ${p.getName()} was received
          std::string ${p.getName()}_uri = "ws://" + message.getIpAndPort() + message.getRemotePort();
          std::cout << "Received connection: " << ${p.getName()}_uri << std::endl;
          comp->addInPort${p.getName()?cap_first}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(INCOMING, ${p.getName()}_uri));
          }
        </#if>
    </#list>
</#macro>

<#macro searchForSubComps comp config>
    <#if comp.getSubComponents()?size == 0>
      // component has no subcomponents - nothing to do
    <#else>

      bool allConnected = 0;
      while (!allConnected)
      {
      std::cout << "Searching for subcomponents\n";
        <#list comp.subComponents as subcomponent>
            <#assign subcomponentSymbol = subcomponent.type.loadedSymbol>
            // ${subcomponentSymbol.getName()} ${subcomponent.getName()}
            <@SCDetailsHelper comp subcomponent/>

            <#if config.getSplittingMode().toString() == "LOCAL">
              std::ifstream i (this->portConfigFilePath);
              json j;
              i >> j;
              std::string ${subcomponent.getName()}_port = j["${subcomponent.getName()}"]["management"].get${"<std::string>"} ();
            <#else>
              std::string ${subcomponent.getName()}_port = "1337";
            </#if>

          std::string ${subcomponent.getName()}_ip = comm->getIpOfComponent ("${subcomponent.getName()}");

          // tell subcomponent where to connect to
          <#list comp.getAstNode().getConnectors() as connector>
              <#list connector.targetList as target>
                  <#if !target.isPresentComponent() && subcomponent.getName() == connector.getSource().getComponent()>
                      <#list subcomponentSymbol.ports as p>
                          <#if p.getName() == connector.getSource().port>
                            // set receiver
                            std::string communicationPort = j["${subcomponent.getName()}"]["communication"].get${"<std::string>"} ();
                            std::string ${subcomponent.getName()}_uri = "ws://" + ${subcomponent.getName()}_ip + ":" + communicationPort + "/" + comp->getInstanceName () + ".${subcomponent.getName()}/out/${p.getName()}";

                            // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
                            comp->addInPort${target.port?cap_first}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(INCOMING, ${subcomponent.getName()}_uri));

                          </#if>
                      </#list>
                  </#if>
              <#-- TODO: What happens if !target.isPresentComponent() --><#if target.isPresentComponent() && subcomponent.getName() == target.getComponent()>
                {
                  <#if !connector.getSource().isPresentComponent()>
                    PortToSocket message ("${target.port}", comm->getOurIp() + ":" + communicationPort, "/" + comp->getInstanceName () + "/out/${connector.getSource().port}");
                  <#else>
                      <#list comp.subComponents as sourceSubcomp>
                          <#if sourceSubcomp.getName() == connector.getSource().getComponent()>
                            std::string communicationPort = j["${connector.getSource().getComponent()}"]["communication"].get${"<std::string>"} ();
                            PortToSocket message ("${target.port}", comp->get${connector.getSource().getComponent()?cap_first}IP() + ":" + communicationPort, "/${sourceSubcomp.fullName}/out/${connector.getSource().port}");
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
    </#if>
</#macro>

<#macro SCDetailsHelper comp subcomponent>
  if (comp->get${subcomponent.getName()?cap_first}IP().length() == 0
    <#list  comp.getAstNode().getConnectors() as connector>
        <#list connector.targetList as target>
        <#-- TODO: What happens when !target.isPresentComponent() -->
            <#if target.isPresentComponent() && subcomponent.getName() == target.getComponent()>
                <#if connector.getSource().isPresentComponent()>
                  && comp->get${connector.getSource().getComponent()?cap_first}IP().length() != 0
                </#if>
            </#if>
        </#list>
    </#list>
  ) {
</#macro>

<#macro initURIs comp>
    <#list comp.ports as p >
      std::string ${p.getName()}_uri;
    </#list>
    <#list comp.subComponents as subcomponent >
      std::string ${subcomponent.getName()}_uri;
    </#list>
    <#list comp.getAstNode().getConnectors() as connector>
        <#list connector.targetList as target>
            <#list comp.subComponents as subcomponent>
                <#assign subcomponentSymbol = subcomponent.type.loadedSymbol>
                <#if !connector.getSource().isPresentComponent() && subcomponent.getName() == target.getComponent()>
                    <#list subcomponentSymbol.ports as p>
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
</#macro>