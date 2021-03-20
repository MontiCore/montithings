<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = compname + "Input">
<#if existsHWC>
  <#assign className += "TOP">
</#if>

#pragma once
#include ${"<string>"}
#include "Port.h"
#include ${"<string>"}
#include ${"<map>"}
#include ${"<vector>"}
#include ${"<list>"}
#include ${"<set>"}
#include ${"<utility>"}
#include ${"<deque>"}
#include "tl/optional.hpp"
${Utils.printIncludes(comp,config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className}
<#if comp.isPresentParentComponent()> :
    ${Utils.printSuperClassFQ(comp)}Input
<#-- TODO Check if comp.parent().loadedSymbol.hasTypeParameter is operational -->
    <#if comp.parent().loadedSymbol.hasTypeParameter><
        <#list ComponentHelper.superCompActualTypeArguments as scTypeParams >
          scTypeParams<#sep>,</#sep>
        </#list>>
    </#if>
</#if>
{
protected:
<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port >
  tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()};
  <#if ComponentHelper.hasAgoQualification(comp, port)>
    static std::deque<std::pair<std::chrono::time_point<std::chrono::system_clock>, ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>> dequeOf__${port.getName()?cap_first};
    std::chrono::nanoseconds highestAgoOf__${port.getName()?cap_first} = std::chrono::nanoseconds {${ComponentHelper.getHighestAgoQualification(comp, port.getName())}};
    void cleanDequeOf${port.getName()?cap_first}(std::chrono::time_point<std::chrono::system_clock> now);
  </#if>
</#list>

<#list ComponentHelper.getPortsInBatchStatement(comp) as port >
  std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()} = {};
</#list>

public:
${className}() = default;
<#if comp.getAllIncomingPorts()?has_content && !isBatch>
  explicit ${className}(
    <#list comp.getAllIncomingPorts() as port>
      tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()}
      <#sep>,</#sep>
    </#list>);
</#if>

<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
    <#if port.isIncoming()>
      tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> get${port.getName()?cap_first}() const;
      void set${port.getName()?cap_first}(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>);
        <#if ComponentHelper.portUsesCdType(port)>
            <#assign cdeImportStatementOpt = ComponentHelper.getCDEReplacement(port, config)>
            <#if cdeImportStatementOpt.isPresent()>
              tl::optional<${cdeImportStatementOpt.get().getImportClass().toString()}> get${port.getName()?cap_first}Adap() const;
              void set${port.getName()?cap_first}(${cdeImportStatementOpt.get().getImportClass().toString()});
            </#if>
        </#if>
        <#if ComponentHelper.hasAgoQualification(comp, port)>
          tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> agoGet${port.getName()?cap_first} (const std::chrono::nanoseconds ago_time);
        </#if>
    </#if>
</#list>

<#list ComponentHelper.getPortsInBatchStatement(comp) as port>
    <#if port.isIncoming()>
      std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> get${port.getName()?cap_first}() const;
      void add${port.getName()?cap_first}Element(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>);
      void set${port.getName()?cap_first}(std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> vector);
    </#if>
</#list>
};

<#if Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.input.generateInputBody", [comp, compname, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}