<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">

    std::vector<sole::uuid> traceUUIDs = {
         <#list comp.getAllIncomingPorts() as inPort>
             input.get${inPort.getName()?cap_first}TraceUUID()
             <#sep>,</#sep>
         </#list>
    };

    this->logTracer->handleInput(input, traceUUIDs);
</#if>
