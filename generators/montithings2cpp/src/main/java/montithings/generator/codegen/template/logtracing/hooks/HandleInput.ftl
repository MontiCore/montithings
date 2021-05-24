<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
    std::multimap<sole::uuid, std::string> traceUUIDs;
    <#list comp.getAllIncomingPorts() as inPort>
        traceUUIDs.insert(std::make_pair(${Identifier.getInputName()}.get${inPort.getName()?cap_first}Uuid(), "${inPort.getName()}"));
    </#list>

    <#if comp.getAllIncomingPorts()?size gt 0>
        this->logTracer->handleInput(${Identifier.getInputName()}, traceUUIDs);
    </#if>
</#if>
