<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled>

  <#if brokerIsDDS>
    cmp.initLogTracer (ddsClient);
  <#elseif brokerIsMQTT>
    cmp.initLogTracer ();
  </#if>

</#if>
