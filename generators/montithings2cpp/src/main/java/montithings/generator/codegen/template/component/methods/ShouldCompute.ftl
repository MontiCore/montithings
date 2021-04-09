<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
bool ${className}${Utils.printFormalTypeParameters(comp)}::shouldCompute() {
<#if comp.getAllIncomingPorts()?size gt 0 && !ComponentHelper.hasSyncGroups(comp)>
    if (timeMode == TIMESYNC || <#list comp.getAllIncomingPorts() as inPort>${Identifier.getInterfaceName()}.getPort${inPort.getName()?cap_first}
    ()->hasValue(this->uuid)<#sep>||</#sep>
</#list>)
    { return true; }
</#if>
<#if ComponentHelper.hasSyncGroups(comp)>
    if (
    <#list ComponentHelper.getSyncGroups(comp) as syncGroup >
        (<#list syncGroup as port >
        getPort${port?cap_first}()->hasValue(this->uuid)<#sep>&&</#sep>
    </#list>)
        <#sep>||</#sep>
    </#list>
    <#if ComponentHelper.getPortsNotInSyncGroup(comp)?size gt 0>
        || <#list ComponentHelper.getPortsNotInSyncGroup(comp) as port > getPort${port.getName()?cap_first}
        ()->hasValue(this->uuid)<#sep>||</#sep>
    </#list>
        <</#if>
    )
    { return true; }
</#if>
<#if comp.getAllIncomingPorts()?size == 0>
    return true;
<#else>
    return false;
</#if>
}