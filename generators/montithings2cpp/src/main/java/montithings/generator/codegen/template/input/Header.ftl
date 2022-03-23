<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#pragma once
#include "Port.h"
#include ${"<string>"}
#include ${"<map>"}
#include ${"<vector>"}
#include ${"<list>"}
#include ${"<set>"}
#include ${"<utility>"}
#include ${"<deque>"}
#include ${"<cereal/access.hpp>"}
#include ${"<cereal/types/string.hpp>"}

#include "collections/Collection.h"
#include "tl/optional.hpp"
#include "Message.h"

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
${tc.includeArgs("template.input.declarations.PortVariables", [comp, config, existsHWC])}
${tc.includeArgs("template.input.methods.Serialize", [comp, config, existsHWC])}

public:
${tc.includeArgs("template.input.declarations.Constructor", [comp, config, existsHWC])}

<#list ComponentHelper.getPortsNotInBatchStatements(comp) as port>
  <#if port.isIncoming()>
    ${tc.includeArgs("template.input.declarations.PortMethods", [port, comp, config, existsHWC])}
  <#else>
    // include ports which are target ports of subcomponents as well
    <#if !comp.isAtomic()>
      <#list comp.getAstNode().getConnectors() as connector>
        <#list connector.getTargetList() as target>
          <#if target.getQName() == port.getName()>
             ${tc.includeArgs("template.input.declarations.PortMethods", [port, comp, config, existsHWC])}
          </#if>
        </#list>
      </#list>
    </#if>
  </#if>
</#list>

<#list ComponentHelper.getPortsInBatchStatement(comp) as port>
  <#if port.isIncoming()>
    ${tc.includeArgs("template.input.declarations.BatchPortMethods", [port, comp, config, existsHWC])}
  <#else>
    // include ports which are target ports of subcomponents as well
    <#if !comp.isAtomic()>
      <#list comp.getAstNode().getConnectors() as connector>
        <#list connector.getTargetList() as target>
          <#if target.getQName() == port.getName()>
             ${tc.includeArgs("template.input.declarations.BatchPortMethodsd bu bi", [port, comp, config, existsHWC])}
          </#if>
        </#list>
      </#list>
    </#if>
  </#if>
</#list>
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.input.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}