<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">

    <#if config.getMessageBroker().toString() == "DDS">
        LogTracerDDSClient logTracerInterface(argc, &argv);
    <#elseif config.getMessageBroker().toString() == "MQTT">
        //TODO
    </#if>

logTracer = new LogTracer(instanceName, logTracerInterface);
</#if>
