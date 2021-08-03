<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","isMonitor","behavior")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if !ComponentHelper.usesBatchMode(comp)>
  ${compname}Input${Utils.printFormalTypeParameters(comp)} ${Identifier.getInputName()}<#if comp.getAllIncomingPorts()?has_content>(<#list comp.getAllIncomingPorts() as inPort >
  <#if behavior != "false" && !ComponentHelper.usesPort(behavior, inPort)>
    tl::nullopt
  <#else>
  <#if ComponentHelper.isSIUnitPort(inPort)>
    ${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}<#else>this->uuid</#if>).value()
                 .applyConversionFactor(this->${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}ConversionFactor())
  <#else>
      ${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}<#else>this->uuid</#if>)
  </#if>

  </#if>
  <#sep>,</#sep>
  </#list>)</#if>;
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
      </#if>));
    }
  </#if>
  </#list>
  <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort >
  <#if behavior == "false" || ComponentHelper.usesPort(behavior, inPort)>
    ${Identifier.getInputName()}.add${inPort.getName()?cap_first}Element(${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}()->getCurrentValue(
    <#if isMonitor>
      portMonitorUuid${inPort.getName()?cap_first}
    <#else>
      this->uuid
    </#if>));
  </#if>
  </#list>
</#if>

