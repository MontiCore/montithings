<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "prefix")}
{
<#list comp.getSubComponents() as subcomp>
  "${subcomp.getName()}": {
  "management": "${config.getComponentPortMap().getManagementPort(prefix + "." + subcomp.getName())}",
  "communication": "${config.getComponentPortMap().getCommunicationPort(prefix + "." + subcomp.getName())}"
  }
  <#sep>,
</#list>
}