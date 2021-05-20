<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
    std::string sourceInstanceName = topic.substr(0, topic.find("/"));
    comp->getLogTracer()->mapPortToSourceInstance("${port.getName()}", sourceInstanceName);
</#if>
