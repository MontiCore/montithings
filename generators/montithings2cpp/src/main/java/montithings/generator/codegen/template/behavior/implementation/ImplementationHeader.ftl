<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#import "/template/behavior/implementation/ImplementationFile.ftl" as ImplementationFile>
<#assign generics = Utils.printFormalTypeParameters(comp)>
<#assign className = compname + "Impl">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#pragma once
#include "${compname}Input.h"
#include "${compname}Result.h"
#include "${compname}State.h"
#include "InOutPort.h"
#include "IComputable.h"
#include ${"<stdexcept>"}
#include "easyloggingpp/easylogging++.h"
${Utils.printIncludes(comp,config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className}
: public IComputable<${compname}Input${generics},${compname}Result${generics}>
{

protected:
std::string instanceName;
${compname}State& ${Identifier.getStateName()};
<#list comp.getOutgoingPorts() as port>
  <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign name = port.getName()>
  InOutPort<${type}>* port${name?cap_first};
</#list>

public:
${className}(${compname}State& state) : ${Identifier.getStateName()}(state) {}

void setInstanceName (const std::string &instanceName);
<#list comp.getOutgoingPorts() as port>
  <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
  <#assign name = port.getName()>
  void setPort${name?cap_first} (InOutPort<${type}> *port${name?cap_first});
</#list>

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
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.behavior.implementation.generateImplementationBody", [comp, compname, className])}
</#if>
${Utils.printNamespaceEnd(comp)}
