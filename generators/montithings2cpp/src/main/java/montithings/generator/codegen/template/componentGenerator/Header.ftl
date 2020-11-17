<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "useWsPorts")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
${Identifier.createInstance(comp)}

#pragma once
#include "IComponent.h"
#include "Port.h"
#include "InOutPort.h"
<#list comp.getPorts() as port>
    <#assign addPort = config.getAdditionalPort(port)>
    <#if addPort!="Optional.empty">
        #include "${Names.getSimpleName(addPort.get())?cap_first}.h"
    </#if>
</#list>
#include ${"<string>"}
#include ${"<map>"}
#include ${"<vector>"}
#include ${"<list>"}
#include ${"<set>"}
#include ${"<thread>"}
#include "sole/sole.hpp"
#include ${"<iostream>"}
${Utils.printIncludes(comp, config)}

<#if comp.isDecomposed()>
  ${Utils.printIncludes(comp, compname, config)}
<#else>
  #include "${compname}Impl.h"
  #include "${compname}Input.h"
  #include "${compname}Result.h"
</#if>

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname} : public IComponent <#if comp.isPresentParentComponent()> , ${Utils.printSuperClassFQ(comp)}
    <#-- TODO Check if comp.parent().loadedSymbol.hasTypeParameter is operational -->
    <#if comp.parent().loadedSymbol.hasTypeParameter><<#list helper.superCompActualTypeArguments as scTypeParams >
      scTypeParams<#sep>,</#sep>
    </#list>>
    </#if></#if>
{
protected:
${tc.includeArgs("template.util.subcomponents.printVars", [comp, comp.getPorts(), config])}
${Utils.printVariables(comp)}

<#-- Currently useless. MontiArc 6's getFields() returns both variables and parameters --><#-- Utils.printConfigParameters(comp) -->
std::vector< std::thread > threads;
TimeMode timeMode = <#if ComponentHelper.isTimesync(comp)>
  TIMESYNC
<#else>
  EVENTBASED
</#if>;
<#if comp.isDecomposed()>
    <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)>
      void run();
    </#if>
    ${tc.includeArgs("template.util.subcomponents.printIncludes", [comp, config])}
<#else>

    ${compname}Impl${Utils.printFormalTypeParameters(comp)} ${Identifier.getBehaviorImplName()};

  void initialize();
  void setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result);
  void run();
</#if>

public:
${tc.includeArgs("template.util.ports.printMethodHeaders", [comp.getPorts(), config])}
${compname}(std::string instanceName<#if comp.getParameters()?has_content>
  ,
</#if>${ComponentHelper.printConstructorArguments(comp)});

<#if comp.isDecomposed()>
    <#if config.getSplittingMode().toString() != "OFF">
        ${tc.includeArgs("template.util.subcomponents.printMethodDeclarations", [comp, config])}
    </#if>
</#if>

void setUp(TimeMode enclosingComponentTiming) override;
void init() override;
void compute() override;
bool shouldCompute();
void start() override;
};

<#if Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.componentGenerator.generateBody", [comp, compname, config])}
</#if>

${Utils.printNamespaceEnd(comp)}
