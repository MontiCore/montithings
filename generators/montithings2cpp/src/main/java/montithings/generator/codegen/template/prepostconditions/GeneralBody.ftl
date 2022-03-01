<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/helper/GeneralPreamble.ftl">

${tc.includeArgs("template.prepostconditions.methods.general.Constructor", [comp, config, isPrecondition, existsHWC])}
${tc.includeArgs("template.prepostconditions.methods.general.IsCatched", [comp, config, isPrecondition, existsHWC])}
${tc.includeArgs("template.prepostconditions.methods.general.Apply", [comp, config, isPrecondition, existsHWC])}
${tc.includeArgs("template.prepostconditions.methods.general.LogError", [comp, config, isPrecondition, existsHWC])}
