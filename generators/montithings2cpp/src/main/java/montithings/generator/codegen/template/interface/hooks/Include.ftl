<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

${tc.includeArgs("template.logtracing.hooks.Include", [config, comp])}

#include "${compname}Interface.h"