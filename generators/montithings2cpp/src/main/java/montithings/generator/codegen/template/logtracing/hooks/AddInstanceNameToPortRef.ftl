<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
    <#if config.getMessageBroker().toString() == "DDS">
        std::string sourceInstanceName = topic.substr(0, topic.find("/"));
        comp->getLogTracer()->mapPortToSourceInstance("${port.getName()}", sourceInstanceName);
    <#elseif config.getMessageBroker().toString() == "MQTT">
         if( topic.find("/connectors/" +  replaceDotsBySlashes (instanceName)) != std::string::npos) {
             std::string sourceInstanceName = getEnclosingComponentName(replaceSlashesByDots(payload));
                          std::string portName = topic.substr(topic.find_last_of("/") +1, topic.length());
             getLogTracer()->mapPortToSourceInstance(portName, sourceInstanceName);
         }
    </#if>
</#if>

