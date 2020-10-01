# (c) https://github.com/MontiCore/monticore

<#--package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper-->

  def static generateInputHeader(ComponentTypeSymbol comp, String compname, ConfigParams config) {
  <#assign helper = ComponentHelper(comp)>
  <#assign isBatch = ComponentHelper.usesBatchMode(comp);>

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
${Ports.printIncludes(comp, config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Input
<#if comp.presentParentComponent> :
  ${Utils.printSuperClassFQ(comp)}Input
<#if comp.parent.loadedSymbol.hasTypeParameter><
<#list helper.superCompActualTypeArguments as scTypeParams >
 scTypeParams<#sep>,
 </#list>></#if>
</#if>
{
private:
<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port >
 tl::optional<${helper.getRealPortCppTypeString(port, config)}> ${port.name};
 </#list>
<#list ComponentHelper.getPortsInBatchStatement(comp) as port >
 std::vector<${helper.getRealPortCppTypeString(port, config)}> ${port.name} = {};
 </#list>
public:
  ${compname}Input() = default;
  <#if !comp.allIncomingPorts.empty && !isBatch>
  explicit ${compname}Input(<#list optional<${helper.getRealPortCppTypeString(port, config)}> ${port.name as port : comp.allIncomingPorts SEPARATOR ','} tl:>

 </#list>);
    </#if>
  <#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
  <#if port.isIncoming>
  tl::optional<${helper.getRealPortCppTypeString(port, config)}> get${port.name.toFirstUpper}() const;
  void set${port.name.toFirstUpper}(tl::optional<${helper.getRealPortCppTypeString(port, config)}>);
  <#if ComponentHelper.portUsesCdType(port)>
  <#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)>
  <#if cdeImportStatementOpt.isPresent()>
  tl::optional<${cdeImportStatementOpt.get.getImportClass().toString()}> get${port.name.toFirstUpper}Adap() const;
  void set${port.name.toFirstUpper}(${cdeImportStatementOpt.get.getImportClass().toString()});
  </#if>
  </#if>
  </#if>
  </#list>
  <#list ComponentHelper.getPortsInBatchStatement(comp) as port>
  <#if port.isIncoming>
  std::vector<${helper.getRealPortCppTypeString(port, config)}> get${port.name.toFirstUpper}() const;
  void add${port.name.toFirstUpper}Element(tl::optional<${helper.getRealPortCppTypeString(port, config)}>);
  void set${port.name.toFirstUpper}(std::vector<${helper.getRealPortCppTypeString(port, config)}> vector);
  </#if>
  </#list>
};

<#if comp.hasTypeParameter>
 ${generateInputBody(comp, compname, config)}
 </#if>
${Utils.printNamespaceEnd(comp)}