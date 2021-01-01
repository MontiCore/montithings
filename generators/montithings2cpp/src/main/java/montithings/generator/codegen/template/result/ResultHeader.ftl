<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}

<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign className = compname + "Result">
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
${Utils.printIncludes(comp, config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className}
<#if comp.isPresentParentComponent()> :
    ${Utils.printSuperClassFQ(comp)}Result
<#-- TODO Check if comp.parent().loadedSymbol.hasTypeParameter is operational -->
    <#if Utils.hasTypeParameter(comp.parent().loadedSymbol)><
        <#list ComponentHelper.superCompActualTypeArguments as scTypeParams >
          scTypeParams<#sep>,</#sep>
        </#list> >
    </#if>
</#if>
{
protected:
<#list comp.getOutgoingPorts() as port >
  tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()};
</#list>
public:
${className}() = default;
<#if !(comp.getAllOutgoingPorts()?size == 0)>
    ${className}(<#list comp.getAllOutgoingPorts() as port > ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}
    ${port.getName()}<#sep>,</#sep>
</#list>);
</#if>
<#list comp.getOutgoingPorts() as port>
  tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> get${port.getName()?cap_first}() const;
  void set${port.getName()?cap_first}(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>);
    <#if ComponentHelper.portUsesCdType(port)>
        <#assign cdeImportStatementOpt = ComponentHelper.getCDEReplacement(port, config)>
        <#if cdeImportStatementOpt.isPresent()>
          tl::optional<${cdeImportStatementOpt.get().getImportClass().toString()}> get${port.getName()?cap_first}Adap() const;
          void set${port.getName()?cap_first}(${cdeImportStatementOpt.get().getImportClass().toString()});
        </#if>
    </#if>
</#list>
};

<#if Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.result.generateResultBody", [comp, compname, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}