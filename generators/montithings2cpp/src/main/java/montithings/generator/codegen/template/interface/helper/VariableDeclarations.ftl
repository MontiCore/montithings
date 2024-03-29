<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

<#list comp.getPorts() as port>
  <#assign type = TypesPrinter.getRealPortCppTypeString(port.getComponent().get(), port, config)>

  <#assign name = port.getName()>
  InOutPort<Message<${type}>>* ${name} = new InOutPort<Message<${type}>>();
  double ${name}ConversionFactor = 1;
</#list>
<#list ComponentHelper.getPortsToTest(comp) as port>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

  <#assign name = port.getName()>
  InOutPort<Message<${type}>>* test__${name} = new InOutPort<Message<${type}>>();
</#list>