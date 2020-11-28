<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getInstances(comp)>

<#list instances as pair >
  <#if config.getMessageBroker().toString() == "MQTT">
  ./${pair.getKey().fullName} ${pair.getValue()} localhost 1883 > ${pair.getValue()}.log 2>&1 &
  <#elseif config.getMessageBroker().toString() == "DDS">
    <#if config.getSplittingMode().toString() == "DISTRIBUTED">
      ./${pair.getKey().fullName} ${pair.getValue()} -DCPSConfigFile dcpsconfig.ini -DCPSInfoRepo localhost:12345 > ${pair.getValue()}.log 2>&1 &
    <#else>
      ./${pair.getKey().fullName} ${pair.getValue()} -DCPSConfigFile dcpsconfig.ini > ${pair.getValue()}.log 2>&1 &
    </#if>
  <#else>
  ./${pair.getKey().fullName} ${pair.getValue()} ${config.getComponentPortMap().getManagementPort(pair.getValue())} ${config.getComponentPortMap().getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
  </#if>
</#list>