# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.ConfigParams-->

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