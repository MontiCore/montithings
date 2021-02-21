<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign isBatch = ComponentHelper.usesBatchMode(comp)>
<#assign className = comp.getName() + "State">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#pragma once
${Utils.printIncludes(comp,config)}
#include "json/json.hpp"
#include ${"<Utils.h>"}
#include ${"<fstream>"}
#include ${"<future>"}
#include ${"<thread>"}
<#if config.getMessageBroker().toString() == "MQTT">
  #include "MqttClient.h"
</#if>

using json = nlohmann::json;


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
<#if config.getMessageBroker().toString() == "MQTT">
  <#if comp.isPresentParentComponent()>,<#else>:</#if>
  public MqttUser
</#if>
{
protected:
${Utils.printVariables(comp, config, false)}
public:
${className} (${Utils.printConfigurationParametersAsList(comp)})
<#if comp.getParameters()?has_content>:</#if>
<#list comp.getParameters() as param >
  ${param.getName()} (${param.getName()})
  <#sep>,</#sep>
</#list>
{}

<#list ComponentHelper.getVariablesAndParameters(comp) as var>
  <#assign varName = var.getName()>
  <#assign varType = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>
  ${varType} get${varName?cap_first} () const;
  void set${varName?cap_first} (${varType} ${varName});
  ${varType} preSet${varName?cap_first} (${varType} ${varName});
  ${varType} postSet${varName?cap_first} (${varType} ${varName});
</#list>

protected:
std::string instanceName;
bool replayFinished = false;
bool replayTimeout = false;
bool receivedState = false;
bool restoredState = false;

public:
void setInstanceName (const std::string &instanceName);
virtual void setup ();
<#if config.getMessageBroker().toString() == "MQTT">
  virtual void requestState ();
  virtual void requestReplay ();
  virtual void publishState (json state);
</#if>
virtual json serializeState ();
virtual void storeState (json state);
virtual bool restoreState ();
virtual bool restoreState (std::string content);

bool isReplayFinished () const;
bool isReplayTimeout () const;
bool isReceivedState () const;
bool isRestoredState () const;

<#if config.getMessageBroker().toString() == "MQTT">
  void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
</#if>
};

<#if Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.state.generateStateBody", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}