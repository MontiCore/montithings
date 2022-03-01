<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/input/helper/GeneralPreamble.ftl">

<#assign name = port.getName()?cap_first>
<#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

std::vector<Message<${type}>> get${name}() const;
void add${name}Element(Message<${type}>);
void set${name}(std::vector<Message<${type}>> vector);