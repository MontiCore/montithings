<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if logTracingEnabled && !hasNoPorts>
  <#list comp.getPorts() as port>
    <#assign additionalPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
    <#if dummyName7>
      this->logTracer->registerExternalPort("${port.getName()}");
    </#if>
  </#list>
</#if>
