# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "compname", "config")}
<#import "/template/util/Ports.ftl" as Ports>
<#--package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper

  <#assign ComponentHelper = statics['montithings.generator.helper.ComponentHelper']>-->
  <#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
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
<@Ports.printIncludes comp config/>

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Input
<#if comp.presentParentComponent> :
  ${Utils.printSuperClassFQ(comp)}Input
<#if comp.parent.loadedSymbol.hasTypeParameter><
<#list ComponentHelper.superCompActualTypeArguments as scTypeParams >
 scTypeParams<#sep>,
 </#list>></#if>
</#if>
{
private:
<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port >
 tl::optional<${ComponentHelper.getRealPortCppTypeString(port, config)}> ${port.name};
 </#list>
<#list ComponentHelper.getPortsInBatchStatement(comp) as port >
 std::vector<${ComponentHelper.getRealPortCppTypeString(port, config)}> ${port.name} = {};
 </#list>
public:
  ${compname}Input() = default;
  <#if !comp.allIncomingPorts.empty && !isBatch>
  explicit ${compname}Input(<#list comp.allIncomingPorts as port> tl:optional<${ComponentHelper.getRealPortCppTypeString(port, config)}> ${port.name}
      <#sep>,
  </#list>);
    </#if>
  <#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
  <#if port.isIncoming>
  tl::optional<${ComponentHelper.getRealPortCppTypeString(port, config)}> get${port.name.toFirstUpper}() const;
  void set${port.name.toFirstUpper}(tl::optional<${ComponentHelper.getRealPortCppTypeString(port, config)}>);
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
  std::vector<${ComponentHelper.getRealPortCppTypeString(port, config)}> get${port.name.toFirstUpper}() const;
  void add${port.name.toFirstUpper}Element(tl::optional<${ComponentHelper.getRealPortCppTypeString(port, config)}>);
  void set${port.name.toFirstUpper}(std::vector<${ComponentHelper.getRealPortCppTypeString(port, config)}> vector);
  </#if>
  </#list>
};

<#if comp.hasTypeParameter>
 ${generateInputBody(comp, compname, config)}
 </#if>
${Utils.printNamespaceEnd(comp)}