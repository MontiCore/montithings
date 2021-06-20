<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
    <#if config.getMessageBroker().toString() == "DDS">
        std::string sourceInstanceName = topic.substr(0, topic.find("/"));
        comp->getLogTracer()->mapPortToSourceInstance("${port.getName()}", sourceInstanceName);
    <#elseif config.getMessageBroker().toString() == "MQTT">
        if( topic.find("/connectors/" +  replaceDotsBySlashes (instanceName)) != std::string::npos) {
            std::string portName = topic.substr(topic.find_last_of("/") +1, topic.length());
            <#if !comp.isAtomic()>
                <#list comp.getIncomingPorts() as p>
                    <#list comp.getAstNode().getConnectors() as connector>
                        <#if connector.getSource().getQName() == p.getName()>
                            <#list connector.getTargetList() as target>
                                getLogTracer()->mapPortToSourceInstance(portName,
                                    instanceName + ".${target.getQName()}");
                            </#list>
                        </#if>

                    </#list>
                </#list>
            <#else>
                std::string sourceInstanceName = replaceSlashesByDots(payload);

                getLogTracer()->mapPortToSourceInstance(portName, sourceInstanceName);
            </#if>
      }
    </#if>
</#if>

