<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "cppRequirements", "existsHWC")}
<#include "/template/Preamble.ftl">

[requires]
<#if brokerIsMQTT>
mosquitto/2.0.10
</#if>
<#if needsNng>
nng/1.3.0
</#if>
<#if needsProtobuf>
protobuf/3.21.1
</#if>
<#list cppRequirements as req >
${req}
</#list>

[generators]
cmake