<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON" && !(comp.getPorts()?size == 0)>
    logTraceObserver->checkInput(${Identifier.getInputName()});
</#if>
