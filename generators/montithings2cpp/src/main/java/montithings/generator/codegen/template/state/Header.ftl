<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/state/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

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

${tc.includeArgs("template.util.statechart.hooks.Enum", [comp, config])}

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
<#list ComponentHelper.getArcFieldVariables(comp) as var>
  ${tc.includeArgs("template.state.declarations.VariableVariables", [var, comp, config, existsHWC])}
</#list>
${tc.includeArgs("template.util.statechart.hooks.Member", [comp, config])}
public:
${tc.includeArgs("template.state.methods.Constructor", [comp, config, existsHWC])}

<#list ComponentHelper.getVariablesAndParameters(comp) as var>
  ${tc.includeArgs("template.state.declarations.VariableMethods", [var, comp, config, existsHWC])}
</#list>
<#list ComponentHelper.getArcFieldVariables(comp) as var>
  ${tc.includeArgs("template.state.declarations.FieldMethods", [var, comp, config, existsHWC])}
</#list>

protected:
std::string instanceName;
bool replayFinished = false;
bool replayTimeout = false;
bool receivedState = false;
bool restoredState = false;
<#if config.getMessageBroker().toString() == "MQTT">
MqttClient* mqttClientInstance;
</#if>

public:
void setInstanceName (const std::string &instanceName);
virtual void setup (<#if config.getMessageBroker().toString() == "MQTT">MqttClient* passedMqttClientInstance</#if>);
<#if config.getMessageBroker().toString() == "MQTT">
  virtual void requestState ();
  virtual void requestReplay ();
  virtual void publishState (json state);
</#if>
${tc.includeArgs("template.util.statechart.hooks.MethodDeclaration", [comp, config])}
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
  ${tc.includeArgs("template.state.Body", [comp, config, className])}
</#if>
${Utils.printNamespaceEnd(comp)}