<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

// Adds port which publishes connectors
std::string topicConnections = "/connectors";
connectorPortOut = std::unique_ptr${"<DDSPort<std::string>>"}(new DDSPort${"<std::string>"}(*this, OUTGOING, topicConnections, true));


// Adds port which subscribes to connectors
connectorPortIn = std::unique_ptr${"<DDSPort<std::string>>"}(new DDSPort${"<std::string>"}(*this, INCOMING, topicConnections, true));

connectorPortIn->addOnDataAvailableCallbackHandler(std::bind(&${comp.getName()}DDSParticipant::onNewConnectors, this, std::placeholders::_1));

<#list comp.getPorts() as p>
    <#assign type = ComponentHelper.getRealPortCppTypeString(comp, p, config)>
    <#assign type = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

    <#if p.isOutgoing()>
      // outgoing port ${p.getName()}
      comp->getInterface()->addOutPort${p.getName()?cap_first}(new DDSPort<${type}>(*this, OUTGOING, comp->getInstanceName() + ".value/out"));
    </#if>
</#list>