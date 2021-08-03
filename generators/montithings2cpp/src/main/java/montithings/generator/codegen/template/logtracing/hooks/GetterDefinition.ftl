<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "hostclassName")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
    ${Utils.printTemplateArguments(comp)}
    LogTracer* ${hostclassName}${Utils.printFormalTypeParameters(comp)}::getLogTracer(){
        return logTracer;
    }
</#if>
