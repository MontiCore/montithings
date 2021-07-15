<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
this->logTracer->getCurrOutputUuid()
</#if>
