# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "config", "prefix")}

<#list comp.getSubComponents() as subcomp>
  "${subcomp.getName()}": {
  "management": "${config.componentPortMap.getManagementPort(prefix + "." + subcomp.getName())}",
  "communication": "${config.componentPortMap.getCommunicationPort(prefix + "." + subcomp.getName())}"
  }
  <sep>,
</#list>