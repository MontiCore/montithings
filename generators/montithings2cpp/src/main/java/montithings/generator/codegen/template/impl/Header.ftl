<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#pragma once
${tc.includeArgs("template.input.hooks.Include", [comp])}
${tc.includeArgs("template.result.hooks.Include", [comp])}
${tc.includeArgs("template.state.hooks.Include", [comp])}
${tc.includeArgs("template.interface.hooks.Include", [comp])}
#include "InOutPort.h"
#include "IComputable.h"
#include ${"<stdexcept>"}
#include "easyloggingpp/easylogging++.h"
${Utils.printIncludes(comp,config)}
#include "MTLibrary.h"

using namespace montithings::library;

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}; // forward declaration to avoid cyclic include

${Utils.printTemplateArguments(comp)}
class ${className}
: public IComputable<${compname}Input${generics},${compname}Result${generics}>
{

protected:
std::string instanceName;
${compname}${generics}& component;
${compname}State${generics}& ${Identifier.getStateName()};
${compname}Interface${generics}& ${Identifier.getInterfaceName()};

public:
${className}(std::string instanceName, ${compname}${generics}& component, ${compname}State${generics}& state, ${compname}Interface${generics}& interface) : instanceName(std::move(instanceName)), component(component), ${Identifier.getStateName()}(state), ${Identifier.getInterfaceName()}(interface) {}

<#if ComponentHelper.hasBehavior(comp)>
  ${compname}Result${generics} getInitialValues() override;
  ${compname}Result${generics} compute(${compname}Input${generics} input) override;
<#else>
  ${compname}Result${generics} getInitialValues() override <#if existsHWC>= 0<#else>{return {};}</#if>;
  ${compname}Result${generics} compute(${compname}Input${generics} input) override <#if existsHWC>= 0<#else>{return {};}</#if>;
</#if>

<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
  <#assign everyBlockName = ComponentHelper.getEveryBlockName(comp, everyBlock)>
  ${compname}Result${generics} compute${everyBlockName}(${compname}Input${generics} input);
</#list>

<#list ComponentHelper.getPortSpecificBehaviors(comp) as behavior>
  <#assign b = ComponentHelper.getBehaviorName(comp, behavior)>
  ${compname}Result${generics} compute${behaviorName}(${compname}Input${generics} input);
</#list>
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.impl.Body", [comp, config, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}
