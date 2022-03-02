<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled && ComponentHelper.componentHasPorts(comp)>
    logTraceObserver->checkOutput();
</#if>
