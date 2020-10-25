<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","isMonitor")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${compname}Result${Utils.printFormalTypeParameters(comp)} result;
<#list comp.getAllOutgoingPorts() as outPort>
    if (getPort${outPort.getName()?cap_first}()->hasValue(<#if isMonitor>portMonitorUuid${outPort.getName()?cap_first}

<#else>
    this->uuid
</#if>)) {
    result.set${outPort.getName()?cap_first}(getPort${outPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${outPort.getName()?cap_first}

<#else>
    this->uuid
</#if>).value());
    }
</#list>