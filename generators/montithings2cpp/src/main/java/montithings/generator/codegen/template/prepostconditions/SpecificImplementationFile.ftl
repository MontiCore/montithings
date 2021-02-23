<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/SpecificPreamble.ftl">


#include "${compname}${prefix}condition${number}.h"

${Utils.printNamespaceStart(comp)}

<#if !Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.prepostconditions.SpecificBody", [comp, statement, config, number, isPrecondition, existsHWC])}
</#if>

${Utils.printNamespaceEnd(comp)}

