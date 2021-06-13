<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">

${tc.includeArgs("template.logtracing.methods.OnEvent", [comp, config, className])}
${tc.includeArgs("template.logtracing.methods.Constructor", [comp, config, className])}

