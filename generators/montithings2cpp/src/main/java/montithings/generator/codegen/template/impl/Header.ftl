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

<#if config.getReplayMode().toString() == "ON">
  #include "dds/replayer/MTReplayLibrary.h"

  using namespace montithings::library::replayer;

  // is read by the hwc interceptor, if ON, system calls are replayed
  #define REPLAY_MODE "ON"
</#if>

// provides nd() method which can be used to wrap non-deterministic calls
#include "dds/recorder/HWCInterceptor.h"
using namespace montithings::library::hwcinterceptor;

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

<#list comp.getOutgoingPorts() as port>
  <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign name = port.getName()>
  InOutPort<${type}>* port${name?cap_first};
</#list>

public:
${className}(std::string instanceName, ${compname}${generics}& component, ${compname}State${generics}& state, ${compname}Interface${generics}& interface) : instanceName(std::move(instanceName)), component(component), ${Identifier.getStateName()}(state), ${Identifier.getInterfaceName()}(interface) {}


void setInstanceName (const std::string &instanceName);
<#list comp.getOutgoingPorts() as port>
  <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign name = port.getName()>
  void setPort${name?cap_first} (InOutPort<${type}> *port${name?cap_first});
</#list>

<#if ComponentHelper.hasBehavior(comp) || ComponentHelper.hasStatechart(comp)>
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
  <#assign behaviorName = ComponentHelper.getPortSpecificBehaviorName(comp, behavior)>
  ${compname}Result${generics} compute${behaviorName}(${compname}Input${generics} input);
</#list>
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.impl.Body", [comp, config, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}
