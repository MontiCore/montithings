<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "comp")}
<#include "/template/Preamble.ftl">
<#if config.getLogTracing().toString() == "ON">
  <#-- for type Pair -->
  #include ${"<"}utility${">"}

  #include "logtracing/Collector.h"
  #include "logtracing/Utils.h"
</#if>
