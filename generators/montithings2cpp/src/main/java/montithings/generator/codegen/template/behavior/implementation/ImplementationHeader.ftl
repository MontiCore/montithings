<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
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
#include "IComputable.h"
#include ${"<stdexcept>"}
#include "json/json.hpp"
#include "easyloggingpp/easylogging++.h"
#include ${"<Utils.h>"}
#include ${"<fstream>"}
#include ${"<future>"}
#include ${"<thread>"}
${Utils.printIncludes(comp,config)}
<#if config.getMessageBroker().toString() == "MQTT">
  #include "MqttClient.h"
</#if>

using json = nlohmann::json;

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className}
: public IComputable<${compname}Input${generics},${compname}Result${generics}>
<#if config.getMessageBroker().toString() == "MQTT">
, public MqttUser
</#if>
{

protected:
std::string instanceName;

bool replayFinished = false;
bool replayTimeout = false;
bool receivedState = false;
bool restoredState = false;


${Utils.printVariables(comp, config)}
<#-- Currently useless. MontiArc 6's getFields() returns both variables and parameters -->
<#-- ${Utils.printConfigParameters(comp)} -->
public:
${tc.includeArgs("template.behavior.implementation.printConstructor", [comp, compname, className])}

void setInstanceName (const std::string &instanceName);

<#list ComponentHelper.getVariablesAndParameters(comp) as var>
  <#assign type = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>
  ${type} get${var.getName()?cap_first}() const;
  void set${var.getName()?cap_first}(${type});
</#list>

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

<#if ComponentHelper.hasBehavior(comp)>
  ${compname}Result${generics} getInitialValues() override;
  ${compname}Result${generics} compute(${compname}Input${generics} input) override;
<#else>
  ${compname}Result${generics} getInitialValues() override = 0;
  ${compname}Result${generics} compute(${compname}Input${generics} input) override = 0;
</#if>
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.behavior.implementation.generateImplementationBody", [comp, compname, className])}
</#if>
${Utils.printNamespaceEnd(comp)}
