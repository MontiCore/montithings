<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

${tc.includeArgs("template.logtracing.hooks.Include", [comp, config])}

#include "${compname}Interface.h"