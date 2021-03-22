<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

<#list comp.getPorts() as port>
  <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign name = port.getName()>
  InOutPort<${type}>* ${name} = new InOutPort<${type}>();
  double ${name}ConversionFactor = 1;
</#list>

<#if comp.isDecomposed()>
  // Internal monitoring of ports (for pre- and postconditions of composed components)
  <#list comp.getPorts() as port>
    <#assign name = port.getName()>
    sole::uuid portMonitorUuid${name?cap_first} = sole::uuid4 ();
  </#list>
</#if>