<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config")}

  <#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
  <#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
  <#assign isBatch = ComponentHelper.usesBatchMode(comp)>

#pragma once
#include <string>
#include "Port.h"
#include <string>
#include <map>
#include <vector>
#include <list>
#include <set>
#include <utility>
#include "tl/optional.hpp"
${Utils.printIncludes(comp,config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Input
<#if comp.isPresentParentComponent()> :
  ${Utils.printSuperClassFQ(comp)}Input
<#if comp.parent().loadedSymbol.hasTypeParameter><
<#list ComponentHelper.superCompActualTypeArguments as scTypeParams >
 scTypeParams<#sep>,
 </#list>></#if>
</#if>
{
private:
<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port >
 tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()};
 </#list>
<#list ComponentHelper.getPortsInBatchStatement(comp) as port >
 std::vector<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()} = {};
 </#list>
public:
  ${compname}Input() = default;
  <#if comp.getAllIncomingPorts()?has_content && !isBatch>
  explicit ${compname}Input(<#list comp.getAllIncomingPorts() as port> tl:optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()}
      <#sep>,
  </#list>);
    </#if>
  <#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
  <#if port.isIncoming()>
  tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> get${port.getName()?cap_first}() const;
  void set${port.getName()?cap_first}(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>);
  <#if ComponentHelper.portUsesCdType(port)>
  <#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)>
  <#if cdeImportStatementOpt.isPresent()>
  tl::optional<${cdeImportStatementOpt.get.getImportClass().toString()}> get${port.getName()?cap_first}Adap() const;
  void set${port.getName()?cap_first}(${cdeImportStatementOpt.get.getImportClass().toString()});
  </#if>
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

<#if comp.hasTypeParameter()>
 ${generateInputBody(comp, compname, config)}
 </#if>
${Utils.printNamespaceEnd(comp)}