# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.ConfigParams-->

class Result {

  def static generateImplementationFile(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    #include "${compname}Result.h"
    ${Utils.printNamespaceStart(comp)}
    <#if !comp.hasTypeParameter>
 ${generateResultBody(comp, compname, config)}
 </#if>
    ${Utils.printNamespaceEnd(comp)}
    '''
  }


  def static generateResultBody(ComponentTypeSymbol comp, String compname, ConfigParams config){
    <#assign ComponentHelper helper = new ComponentHelper(comp)>
      return '''
<#if !comp.allOutgoingPorts.empty>
${Utils.printTemplateArguments(comp)}
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::${compname}Result(<#list comp.allOutgoingPorts} ${helper.getRealPortCppTypeString(port, config) as port >
 port.name<#sep>,
 </#list>){
  <#if comp.presentParentComponent>
  super(<#list comp.parent.loadedSymbol.allOutgoingPorts as port >
 port.name
 </#list>);
</#if>
<#list comp.outgoingPorts as port >
 this->${port.name} = ${port.name};
 </#list>
}
</#if>

<#list comp.outgoingPorts as port>
${Utils.printTemplateArguments(comp)}
tl::optional<${helper.getRealPortCppTypeString(port, config)}>
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::get${port.name.toFirstUpper}() const
{
  return ${port.name};
}

${Utils.printTemplateArguments(comp)}
void 
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::set${port.name.toFirstUpper}(tl::optional<${helper.getRealPortCppTypeString(port, config)}> ${port.name})
{
  this->${port.name} = ${port.name};
}
<#if ComponentHelper.portUsesCdType(port)>
<#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)>
<#if cdeImportStatementOpt.isPresent()>
 <#assign fullImportStatemantName = cdeImportStatementOpt.get.getSymbol.getFullName.split("\\.")>
 <#assign adapterName = fullImportStatemantName.get(0)+"Adapter">

tl::optional<${cdeImportStatementOpt.get.getImportClass().toString()}>
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::get${port.name.toFirstUpper}Adap() const
{
  if (!get${port.name.toFirstUpper}().has_value()) {
          return {};
      }

      ${adapterName.toFirstUpper} ${adapterName.toFirstLower};
      return ${adapterName.toFirstLower}.convert(*get${port.name.toFirstUpper}());
}

void
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::set${port.name.toFirstUpper}(${cdeImportStatementOpt.get.getImportClass().toString()} element)
{
  ${adapterName.toFirstUpper} ${adapterName.toFirstLower};
      this->${port.name} = ${adapterName.toFirstLower}.convert(element);
}

</#if>
</#if>
</#list>
'''
  }

  def static generateResultHeader(ComponentTypeSymbol comp, String compname, ConfigParams config){
      <#assign ComponentHelper helper = new ComponentHelper(comp)>
return '''
#pragma once
#include <string>
#include "Port.h"
#include <string>
#include <map>
#include <vector>
#include <list>
#include <set>
${Ports.printIncludes(comp, config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Result
  <#if comp.presentParentComponent> :
    ${Utils.printSuperClassFQ(comp)}Result
    <#if comp.parent.loadedSymbol.hasTypeParameter()><
    <#list helper.superCompActualTypeArguments as scTypeParams >
 scTypeParams<#sep>,
 </#list> > </#if>
    </#if>
{
private:
  <#list comp.outgoingPorts as port >
 tl::optional<${helper.getRealPortCppTypeString(port, config)}> ${port.name};
 </#list>
public:	
  ${compname}Result() = default;
  <#if !comp.allOutgoingPorts.empty>
  ${compname}Result(<#list comp.allOutgoingPorts} ${helper.getRealPortCppTypeString(port, config) as port >
 port.name<#sep>,
 </#list>);
  </#if>
  <#list comp.outgoingPorts as port>
  tl::optional<${helper.getRealPortCppTypeString(port, config)}> get${port.name.toFirstUpper}() const;
  void set${port.name.toFirstUpper}(tl::optional<${helper.getRealPortCppTypeString(port, config)}>);
  <#if ComponentHelper.portUsesCdType(port)>
    <#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)>
    <#if cdeImportStatementOpt.isPresent()>
    tl::optional<${cdeImportStatementOpt.get.getImportClass().toString()}> get${port.name.toFirstUpper}Adap() const;
    void set${port.name.toFirstUpper}(${cdeImportStatementOpt.get.getImportClass().toString()});
  </#if>
  </#if>
  </#list>
};

<#if comp.hasTypeParameter()>
 ${generateResultBody(comp, compname, config)}
 </#if>
${Utils.printNamespaceEnd(comp)}
'''

  }
}