<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">

${className}() = default;
<#if !(comp.getAllOutgoingPorts()?size == 0)>
    ${className}(<#list comp.getAllOutgoingPorts() as port > ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}
    ${port.getName()}<#sep>,</#sep>
</#list>);
</#if>