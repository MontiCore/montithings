<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">

<#assign name = port.getName()>

<#if config.getLogTracing().toString() == "ON">
  sole::uuid get${name?cap_first}TraceUUID();
</#if>
