<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "prefix", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/util/comm/helper/GeneralPreamble.ftl">

{
"generated-using": "MontiThings, a www.MontiCore.de technology",
<#list comp.getSubComponents() as subcomp>
  "${subcomp.getName()}": {
  "management": "${config.getComponentPortMap().getManagementPort(prefix + "." + subcomp.getName())}",
  "communication": "${config.getComponentPortMap().getCommunicationPort(prefix + "." + subcomp.getName())}"
  }
  <#sep>,</#sep>
</#list>
}