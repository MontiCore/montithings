<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/prepostconditions/helper/SpecificPreamble.ftl">

${tc.includeArgs("template.prepostconditions.methods.specific.Check", [comp, statement, config, number, isPrecondition, existsHWC])}
${tc.includeArgs("template.prepostconditions.methods.specific.Resolve", [comp, statement, config, number, isPrecondition, existsHWC])}
${tc.includeArgs("template.prepostconditions.methods.specific.ToString", [comp, statement, config, number, isPrecondition, existsHWC])}
${tc.includeArgs("template.prepostconditions.methods.specific.IsCatched", [comp, statement, config, number, isPrecondition, existsHWC])}
