<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">
<#if config.getLogTracing().toString() == "ON">
  <#-- for type Pair -->
  #include ${"<"}utility${">"}

  #include "logtracing/LogTracer.h"
</#if>
