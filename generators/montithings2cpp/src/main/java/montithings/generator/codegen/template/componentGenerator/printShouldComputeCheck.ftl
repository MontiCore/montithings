${tc.signature("comp","compname")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
bool ${compname}${Utils.printFormalTypeParameters(comp)}::shouldCompute() {
<#if comp.getAllIncomingPorts()?size gt 0 && !ComponentHelper.hasSyncGroups(comp)>
    if (timeMode == TIMESYNC || <#list comp.getAllIncomingPorts() as inPort>getPort${inPort.getName()?cap_first}
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