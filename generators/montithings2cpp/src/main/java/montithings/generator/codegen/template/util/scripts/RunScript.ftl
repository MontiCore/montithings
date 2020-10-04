# (c) https://github.com/MontiCore/monticore

<#assign instances = ComponentHelper.getInstances(comp)>

<#list instances as pair >
  ./${pair.getKey().fullName} ${pair.getValue()} ${config.componentPortMap.getManagementPort(pair.getValue())} ${config.componentPortMap.getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
</#list>