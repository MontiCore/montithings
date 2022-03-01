<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "existsHWC")}
<#include "/template/Preamble.ftl">

[requires]
<#if brokerIsMQTT>
mosquitto/2.0.10
</#if>
<#if needsNng>
nng/1.3.0
</#if>

[generators]
cmake