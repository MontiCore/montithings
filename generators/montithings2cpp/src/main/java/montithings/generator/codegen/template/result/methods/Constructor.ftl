<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/result/helper/GeneralPreamble.ftl">

<#if !(comp.getAllOutgoingPorts()?size == 0)>
  ${Utils.printTemplateArguments(comp)}
  ${className}${Utils.printFormalTypeParameters(comp, false)}::${className}
  (
  <#list comp.getAllOutgoingPorts() as port>
    <#assign type = TypesPrinter.getRealPortCppTypeString(comp, port, config)>

    ${type} ${port.getName()} <#sep>,</#sep>
  </#list>
  ){
  <#if comp.isPresentParentComponent()>
    super(<#list comp.parent().loadedSymbol.getAllOutgoingPorts() as port >
    port.getName()
    </#list>);
  </#if>
  <#list comp.getOutgoingPorts() as port >
    this->${port.getName()} = ${port.getName()};
    <#if ComponentHelper.hasAgoQualification(comp, port)>
      auto nowOf__${port.getName()?cap_first} = std::chrono::system_clock::now();
      dequeOf__${port.getName()?cap_first}.push_back(std::make_pair(nowOf__${port.getName()?cap_first}, ${port.getName()}));
      cleanDequeOf${port.getName()?cap_first}(nowOf__${port.getName()?cap_first});
    </#if>
  </#list>
  }
</#if>