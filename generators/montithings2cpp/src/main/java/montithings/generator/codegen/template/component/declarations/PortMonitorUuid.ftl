<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">


<#if comp.isDecomposed()>
  // Internal monitoring of ports (for pre- and postconditions of composed components)
  <#list comp.getPorts() as port>
    <#assign name = port.getName()>
    sole::uuid portMonitorUuid${name?cap_first} = sole::uuid4 ();
  </#list>
</#if>