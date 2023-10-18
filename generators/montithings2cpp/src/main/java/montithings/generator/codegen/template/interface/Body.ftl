<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">

<#list comp.getPorts() as port>
  <#assign type = TypesPrinter.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign type = "Message<" + type + ">">
  <#assign name = port.getName()>
  ${tc.includeArgs("template.interface.methods.GetPort", [comp, config, name, type, existsHWC])}
  ${tc.includeArgs("template.interface.methods.AddInPort", [comp, config, name, type, existsHWC])}
  ${tc.includeArgs("template.interface.methods.RemoveInPort", [comp, config, name, type, existsHWC])}
  ${tc.includeArgs("template.interface.methods.AddOutPort", [comp, config, name, type, existsHWC])}
  ${tc.includeArgs("template.interface.methods.RemoveOutPort", [comp, config, name, type, existsHWC])}
  ${tc.includeArgs("template.interface.methods.GetPortConversionFactor", [comp, config, name, type, existsHWC])}
  ${tc.includeArgs("template.interface.methods.SetPortConversionFactor", [comp, config, name, type, existsHWC])}
</#list>
<#list ComponentHelper.getPortsToTest(comp) as port>
    <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>
    <#assign type = "Message<" + type + ">">
    <#assign name = "test__" + port.getName()>
    ${tc.includeArgs("template.interface.methods.GetPort", [comp, config, name, type, existsHWC])}
    ${tc.includeArgs("template.interface.methods.AddInPort", [comp, config, name, type, existsHWC])}
    ${tc.includeArgs("template.interface.methods.RemoveInPort", [comp, config, name, type, existsHWC])}
    ${tc.includeArgs("template.interface.methods.AddOutPort", [comp, config, name, type, existsHWC])}
    ${tc.includeArgs("template.interface.methods.RemoveOutPort", [comp, config, name, type, existsHWC])}
</#list>