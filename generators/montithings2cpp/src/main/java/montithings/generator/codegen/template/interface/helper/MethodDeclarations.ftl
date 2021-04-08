<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

<#list comp.getPorts() as port>
  <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign name = port.getName()>
  InOutPort<${type}>* getPort${name?cap_first}();
  void addInPort${name?cap_first}(Port<${type}>* ${name});
  void removeInPort${name?cap_first}(Port<${type}>* ${name});
  void addOutPort${name?cap_first}(Port<${type}>* ${name});
  void removeOutPort${name?cap_first}(Port<${type}>* ${name});
  void setPort${name?cap_first}ConversionFactor(double ${name}ConversionFactor);
  double getPort${name?cap_first}ConversionFactor();
</#list>