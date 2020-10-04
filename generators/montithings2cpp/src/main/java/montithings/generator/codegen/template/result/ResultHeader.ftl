<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config")}
<#--package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.ComponentHelper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.ConfigParams-->

<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

#pragma once
#include <string>
#include "Port.h"
#include <string>
#include <map>
#include <vector>
#include <list>
#include <set>
${Utils.printIncludes(comp, config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Result
  <#if comp.isPresentParentComponent()> :
    ${Utils.printSuperClassFQ(comp)}Result
    <#if comp.parent().loadedSymbol.hasTypeParameter()><
    <#list ComponentHelper.superCompActualTypeArguments as scTypeParams >
 scTypeParams<#sep>,
 </#list> > </#if>
    </#if>
{
private:
  <#list comp.getOutgoingPorts() as port >
 tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()};
 </#list>
public:	
  ${compname}Result() = default;
  <#if !comp.getAllOutgoingPorts()?has_content>
  ${compname}Result(<#list comp.getAllOutgoingPorts() as port > ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}
 ${port.getName()}<#sep>,
 </#list>);
  </#if>
  <#list comp.getOutgoingPorts() as port>
  tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> get${port.getName()?cap_first}() const;
  void set${port.getName()?cap_first}(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>);
  <#if ComponentHelper.portUsesCdType(port)>
    <#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)>
    <#if cdeImportStatementOpt.isPresent()>
    tl::optional<${cdeImportStatementOpt.get.getImportClass().toString()}> get${port.getName()?cap_first}Adap() const;
    void set${port.getName()?cap_first}(${cdeImportStatementOpt.get.getImportClass().toString()});
  </#if>
  </#if>
  </#list>
};

<#if comp.hasTypeParameter()>
 ${generateResultBody(comp, compname, config)}
 </#if>
${Utils.printNamespaceEnd(comp)}