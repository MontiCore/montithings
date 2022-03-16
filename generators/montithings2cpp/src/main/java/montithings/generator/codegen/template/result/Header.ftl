<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#pragma once
#include "Port.h"
#include ${"<string>"}
#include ${"<map>"}
#include ${"<vector>"}
#include ${"<list>"}
#include ${"<set>"}
#include ${"<deque>"}
#include "collections/Set.h"

#include "Message.h"

${tc.includeArgs("template.logtracing.hooks.Include", [comp, config])}

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
  ${tc.includeArgs("template.result.declarations.PortVariables", [port, comp, config, existsHWC])}
</#list>
public:
${tc.includeArgs("template.result.declarations.Constructor", [comp, config, existsHWC])}
<#list comp.getOutgoingPorts() as port>
  ${tc.includeArgs("template.result.declarations.PortMethods", [port, comp, config, existsHWC])}
</#list>
};

<#if Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.result.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}