<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

// Adds port which publishes connectors
std::string topicConnections = "/connectors";
connectorPortOut = std::unique_ptr${"<DDSPort<std::string>>"}(new DDSPort${"<std::string>"}(*this, OUTGOING, topicConnections, true));


// Adds port which subscribes to connectors
connectorPortIn = std::unique_ptr${"<DDSPort<std::string>>"}(new DDSPort${"<std::string>"}(*this, INCOMING, topicConnections, true));

connectorPortIn->addOnDataAvailableCallbackHandler(std::bind(&${comp.getName()}DDSParticipant::onNewConnectors, this, std::placeholders::_1));

<#list comp.getPorts() as p>
    <#if p.isOutgoing()>
      // outgoing port ${p.getName()}
      comp->interface.addOutPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, OUTGOING, comp->getInstanceName() + ".value/out"));
    </#if>
</#list>