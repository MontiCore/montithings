# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getInstances(comp)>

<#list instances as pair >
  ./${pair.getKey().fullName} ${pair.getValue()} ${config.getComponentPortMap().getManagementPort(pair.getValue())} ${config.getComponentPortMap().getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
</#list>