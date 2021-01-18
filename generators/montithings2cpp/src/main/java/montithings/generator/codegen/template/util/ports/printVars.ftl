<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","ports","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
// Ports
<#list ports as port>
  <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign name = port.getName()>
  InOutPort<${type}>* ${name} = new InOutPort<${type}>();
</#list>

<#list ports as port>
  <#assign name = port.getName()>
  int ${name}ConversionFactor = 1;
</#list>

<#if comp.isDecomposed()>
  // Internal monitoring of ports (for pre- and postconditions of composed components)
  <#list ports as port>
    <#assign name = port.getName()>
    sole::uuid portMonitorUuid${name?cap_first} = sole::uuid4 ();
  </#list>
</#if>