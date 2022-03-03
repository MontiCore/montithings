<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled>

  <#if brokerIsDDS>
    void initLogTracer (DDSClient &ddsClient);
  <#elseif brokerIsMQTT>
    void initLogTracer ();
  </#if>

</#if>
