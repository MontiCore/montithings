<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "port")}
<#include "/template/Preamble.ftl">

<#assign name = port.getName()>

<#if config.getLogTracing().toString() == "ON">
  sole::uuid
  ${className}${Utils.printFormalTypeParameters(comp, false)}::get${name?cap_first}TraceUUID()
  {
      return ${name}->first;
  }
</#if>
