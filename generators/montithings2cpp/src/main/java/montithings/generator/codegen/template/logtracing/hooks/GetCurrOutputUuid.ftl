<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled>
this->logTracer->getCurrOutputUuid()
</#if>
