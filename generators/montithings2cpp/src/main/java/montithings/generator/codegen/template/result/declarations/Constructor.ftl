<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">

${className}() = default;
<#if !(comp.getAllOutgoingPorts()?size == 0)>
    ${className}(<#list comp.getAllOutgoingPorts() as port >
        <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>
        ${type}
        ${port.getName()}<#sep>,</#sep>
</#list>);
</#if>