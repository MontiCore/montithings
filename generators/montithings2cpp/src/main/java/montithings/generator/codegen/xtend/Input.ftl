# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper-->

class Input {

  def static generateImplementationFile(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    #include "${compname}Input.h"
    ${Utils.printNamespaceStart(comp)}
    <#if !comp.hasTypeParameter>
 ${generateInputBody(comp, compname, config)}
 </#if>
    ${Utils.printNamespaceEnd(comp)}
    '''
  }


    def static generateInputBody(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    <#assign ComponentHelper helper = new ComponentHelper(comp)>
    <#assign isBatch = ComponentHelper.usesBatchMode(comp);>
    
    return '''
<#if !isBatch>

<#if !comp.allIncomingPorts.empty>
${Utils.printTemplateArguments(comp)}
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::${compname}Input(<#list optional<${helper.getRealPortCppTypeString(port, config)}> ${port.name as port : comp.allIncomingPorts SEPARATOR ','} tl:>

 </#list>){
<#if comp.presentParentComponent>
  super(<#list comp.parent.loadedSymbol.allIncomingPorts as port >
 port.name
 </#list>);
</#if>
<#list comp.incomingPorts as port >
 this->${port.name} = std::move(${port.name});
 </#list>
}
</#if>
</#if>
<#list ComponentHelper.getPortsInBatchStatement(comp) as port>
<#if port.isIncoming>
${Utils.printTemplateArguments(comp)}
std::vector<${helper.getRealPortCppTypeString(port, config)}>
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::get${port.name.toFirstUpper}() const
{
  return ${port.name};
}

${Utils.printTemplateArguments(comp)}
void 
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::add${port.name.toFirstUpper}Element(tl::optional<${helper.getRealPortCppTypeString(port, config)}> element)
{
  if (element)
    {
    ${port.name}.push_back(element.value());
     }
}

${Utils.printTemplateArguments(comp)}
void 
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::set${port.name.toFirstUpper}(std::vector<${helper.getRealPortCppTypeString(port, config)}> vector)
{
  this->${port.name} = std::move(vector);
}
</#if>
</#list>

<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
<#if port.isIncoming>
${Utils.printTemplateArguments(comp)}
tl::optional<${helper.getRealPortCppTypeString(port, config)}>
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::get${port.name.toFirstUpper}() const
{
  return ${port.name};
}

${Utils.printTemplateArguments(comp)}
void 
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::set${port.name.toFirstUpper}(tl::optional<${helper.getRealPortCppTypeString(port, config)}> element)
{
  this->${port.name} = std::move(element);
}

<#if ComponentHelper.portUsesCdType(port)>
<#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)>
<#if cdeImportStatementOpt.isPresent()>
 <#assign fullImportStatemantName = cdeImportStatementOpt.get.getSymbol.getFullName.split("\\.")>
 <#assign adapterName = fullImportStatemantName.get(0)+"Adapter">

tl::optional<${cdeImportStatementOpt.get.getImportClass().toString()}>
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::get${port.name.toFirstUpper}Adap() const
{
  if (!get${port.name.toFirstUpper}().has_value()) {
          return {};
      }

      ${adapterName.toFirstUpper} ${adapterName.toFirstLower};
      return ${adapterName.toFirstLower}.convert(*get${port.name.toFirstUpper}());
}

void
${compname}Input${Utils.printFormalTypeParameters(comp, false)}::set${port.name.toFirstUpper}(${cdeImportStatementOpt.get.getImportClass().toString()} element)
{
  ${adapterName.toFirstUpper} ${adapterName.toFirstLower};
      this->${port.name} = ${adapterName.toFirstLower}.convert(element);
}

</#if>
</#if>
</#if>
</#list>

'''
  }

  def static generateInputHeader(ComponentTypeSymbol comp, String compname, ConfigParams config) {
  <#assign ComponentHelper helper = new ComponentHelper(comp)>
  <#assign isBatch = ComponentHelper.usesBatchMode(comp);>

    return '''
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
'''
  }

}