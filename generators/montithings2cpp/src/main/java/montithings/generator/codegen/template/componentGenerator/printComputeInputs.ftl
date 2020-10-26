<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","isMonitor")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
    <#if !ComponentHelper.usesBatchMode(comp)>
        ${compname}Input${Utils.printFormalTypeParameters(comp)} input<#if comp.getAllIncomingPorts()?has_content>(<#list comp.getAllIncomingPorts() as inPort >getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}<#else>this->uuid</#if>
        )<#sep>,</#sep>
    </#list>)</#if>;
    <#else>
        ${compname}Input${Utils.printFormalTypeParameters(comp)} input;
        <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
            while(getPort${inPort.getName()?cap_first}()->hasValue(this->uuid)){
            input.add${inPort.getName()?cap_first}Element(getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}

        <#else>
            this->uuid
        </#if>));
            }
        </#list>
        <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort >
            input.add${inPort.getName()?cap_first}Element(getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}

        <#else>
            this->uuid
        </#if>));
        </#list>
    </#if>