<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled && !hasNoPorts>
    logTraceObserver->checkOutput();
</#if>
