<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "useWsPorts", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign GeneratorHelper = tc.instantiate("montithings.generator.helper.GeneratorHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>
<#assign className = compname>
<#if existsHWC>
    <#assign className += "TOP">
</#if>
${Identifier.createInstance(comp)}

#pragma once
#include "IComponent.h"
#include "Port.h"
#include "InOutPort.h"
<#list comp.getPorts() as port>
    <#assign addPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
    <#if config.getTemplatedPorts()?seq_contains(port) && addPort!="Optional.empty">
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
#include "easyloggingpp/easylogging++.h"
#include ${"<iostream>"}
<#if config.getMessageBroker().toString() == "MQTT">
  #include "MqttClient.h"
  #include "MqttPort.h"
  #include "Utils.h"
</#if>
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
class ${className} : public IComponent
<#if config.getMessageBroker().toString() == "MQTT">
    , public MqttUser
</#if>
<#if comp.isPresentParentComponent()>
  , ${Utils.printSuperClassFQ(comp)}
  <#-- TODO Check if comp.parent().loadedSymbol.hasTypeParameter is operational -->
  <#if comp.parent().loadedSymbol.hasTypeParameter><<#list helper.superCompActualTypeArguments as scTypeParams >
    scTypeParams<#sep>,</#sep>
  </#list>>
  </#if>

</#if>
{
protected:
${tc.includeArgs("template.util.ports.printVars", [comp, comp.getPorts(), config])}
${Utils.printVariables(comp, config)}

<#-- Currently useless. MontiArc 6's getFields() returns both variables and parameters -->
<#-- Utils.printConfigParameters(comp) -->
std::vector< std::thread > threads;
std::mutex computeMutex;
TimeMode timeMode = 
<#if ComponentHelper.isTimesync(comp)>
  TIMESYNC
<#else>
  EVENTBASED
</#if>;
<#if comp.isDecomposed()>
    <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config)>
      void run();
    </#if>
    ${tc.includeArgs("template.util.subcomponents.printIncludes", [comp, config])}
<#else>
  ${compname}Impl${Utils.printFormalTypeParameters(comp)} ${Identifier.getBehaviorImplName()};

  void initialize();
  void setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result);
  void run();
  <#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
  void run_Every${everyBlock.getName()}();
  </#list>
</#if>

public:
${tc.includeArgs("template.util.ports.printMethodHeaders", [comp.getPorts(), config])}
${className}(std::string instanceName<#if comp.getParameters()?has_content>
  ,
</#if>${ComponentHelper.printConstructorArguments(comp)});

<#if config.getMessageBroker().toString() == "MQTT">
  void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
  void publishConnectors();
  void publishConfigForSubcomponent (std::string instanceName);
</#if>

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
void onEvent () override;
<#if ComponentHelper.retainState(comp)>
  bool restoreState ();
</#if>
void threadJoin ();
};

<#if Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.componentGenerator.generateBody", [comp, compname, config, className])}
</#if>

${Utils.printNamespaceEnd(comp)}
