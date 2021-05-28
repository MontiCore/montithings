<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${className}LogTraceObserver::${className}LogTraceObserver (${className} *comp)
    : comp(comp) {
        ${tc.includeArgs("template.logtracing.helper.GenerateAttachStatements", [comp, config])}
}