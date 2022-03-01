<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled && !(comp.getPorts()?size == 0)>
    logTraceObserver->checkOutput();
</#if>
