<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","isMonitor","behavior")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if !ComponentHelper.usesBatchMode(comp)>
  ${compname}Input${Utils.printFormalTypeParameters(comp)} ${Identifier.getInputName()}<#if ComponentHelper.componentHasIncomingPorts(comp)>(<#list comp.getAllIncomingPorts() as inPort >
  <#if behavior != "false" && !ComponentHelper.usesPort(behavior, inPort)>
    tl::nullopt
  <#else>
    ${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}<#else>this->uuid</#if>)
  </#if>
  <#sep>,</#sep>
  </#list>)</#if>;
  <#list comp.getAllIncomingPorts() as inPort>
  <#if ComponentHelper.isSIUnitPort(inPort)>
    if (${Identifier.getInputName()}.get${inPort.getName()?cap_first}().has_value()) {
    ${Identifier.getInputName()}.set${inPort.getName()?cap_first}(${Identifier.getInputName()}.get${inPort.getName()?cap_first}().value() *
    ${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}ConversionFactor());
    }
  </#if>
  </#list>
<#else>
  ${compname}Input${Utils.printFormalTypeParameters(comp)} ${Identifier.getInputName()};
  <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
  <#if behavior == "false" || ComponentHelper.usesPort(behavior, inPort)>
    while(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->hasValue(this->uuid)){
    ${Identifier.getInputName()}.add${inPort.getName()?cap_first}Element(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(
      <#if isMonitor>
        portMonitorUuid${inPort.getName()?cap_first}
      <#else>
        this->uuid
      </#if>).value());
    }
  </#if>
  </#list>
  <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort >
  <#if behavior == "false" || ComponentHelper.usesPort(behavior, inPort)>
    ${Identifier.getInputName()}.set${inPort.getName()?cap_first}(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(
    <#if isMonitor>
      portMonitorUuid${inPort.getName()?cap_first}
    <#else>
      this->uuid
    </#if>).value());
  </#if>
  </#list>
</#if>

