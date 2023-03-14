<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

<#list comp.getPorts() as port>
  <#assign type = TypesPrinter.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign type = "Message<" + type + ">">
  <#assign name = port.getName()>
  InOutPort<${type}>* getPort${name?cap_first}();
  void addInPort${name?cap_first}(Port<${type}>* ${name});
  void removeInPort${name?cap_first}(Port<${type}>* ${name});
  void addOutPort${name?cap_first}(Port<${type}>* ${name});
  void removeOutPort${name?cap_first}(Port<${type}>* ${name});
  void setPort${name?cap_first}ConversionFactor(double ${name}ConversionFactor);
  double getPort${name?cap_first}ConversionFactor();
</#list>
<#list ComponentHelper.getPortsToTest(comp) as port>
  <#assign type = TypesPrinter.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign type = "Message<" + type + ">">
  <#assign name = port.getName()>
  InOutPort<${type}>* getPortTest__${name?cap_first}();
  void addInPortTest__${name?cap_first}(Port<${type}>* ${name});
  void removeInPortTest__${name?cap_first}(Port<${type}>* ${name});
  void addOutPortTest__${name?cap_first}(Port<${type}>* ${name});
  void removeOutPortTest__${name?cap_first}(Port<${type}>* ${name});
</#list>