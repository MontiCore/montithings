<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/logtracing/helper/GeneralPreamble.ftl">

${className}::${className} (${compname} *comp)
    : comp(comp) {
        ${tc.includeArgs("template.logtracing.helper.GenerateAttachStatements", [comp, config])}
}