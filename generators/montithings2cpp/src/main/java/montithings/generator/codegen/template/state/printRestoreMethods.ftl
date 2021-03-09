<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign generics = Utils.printFormalTypeParameters(comp)>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setup ()
{
<#if config.getMessageBroker().toString() == "MQTT">
  std::string instanceNameTopic = replaceDotsBySlashes (this->instanceName);
  MqttClient::instance ()->addUser (this);
  MqttClient::instance ()->subscribe ("/state/" + instanceNameTopic);
  MqttClient::instance ()->subscribe ("/replayFinished/" + instanceNameTopic);
</#if>
<#list ComponentHelper.getArcFieldVariables(comp) as var>
  <#assign varName = var.getName()>
  <#assign type = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>
  vectorOf__${varName?cap_first}.push_back(std::make_pair(std::chrono::system_clock::now(), ${Utils.getInitialValue(var)}));
</#list>
}

<#if config.getMessageBroker().toString() == "MQTT">
  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::requestState ()
  {
  MqttClient::instance ()->publish ("/getState/" + replaceDotsBySlashes (this->instanceName), "");
  }

  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::requestReplay ()
  {
  MqttClient::instance ()->publish ("/requestReplay/" + replaceDotsBySlashes (this->instanceName), "");
  }

  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::publishState (json state)
  {
  MqttClient::instance ()->publish ("/setState/" + replaceDotsBySlashes (this->instanceName), state.dump ());
  }
</#if>

${Utils.printTemplateArguments(comp)}
json ${className}${generics}::serializeState ()
{
json state;
<#list ComponentHelper.getFields(comp) as variable>
  state["${variable.getName()}"] = dataToJson (${variable.getName()});
</#list>
return state;
}

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::storeState (json state)
{
// empty file
std::ofstream storeFile;
storeFile.open (this->instanceName + ".json", std::ofstream::out | std::ofstream::trunc);
storeFile.close ();

// store state
storeFile.open (this->instanceName + ".json", std::ios_base::app | std::ios_base::out);
storeFile << state.dump ();
storeFile.close ();
}

${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::restoreState ()
{
// read in file
std::ifstream storeFile(this->instanceName + ".json");
std::string content;
while(std::getline(storeFile, content))
;
return restoreState (content);
}


${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::restoreState (std::string content)
{
try
{
json state = json::parse (content);

// set state
<#list ComponentHelper.getFields(comp) as variable>
    ${variable.getName()} = jsonToData${"<"}${ComponentHelper.printCPPTypeName(variable.getType())}${">"}(state["${variable.getName()}"]);
</#list>
this->restoredState = true;
return true;
}
catch (nlohmann::detail::parse_error &error)
{
return false;
}
}

${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::isReplayFinished () const
{
return replayFinished;
}

${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::isReplayTimeout () const
{
return replayTimeout;
}

${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::isReceivedState () const
{
return receivedState;
}

${Utils.printTemplateArguments(comp)}
bool ${className}${generics}::isRestoredState () const
{
return restoredState;
}

<#if config.getMessageBroker().toString() == "MQTT">
  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
  {
  std::string topic = std::string ((char *)message->topic);
  std::string payload = std::string ((char *)message->payload, message->payloadlen);

  if (topic == "/state/" + replaceDotsBySlashes (this->instanceName))
  {
  if (payload != "none")
  {
  restoreState (payload);
  }
  this->receivedState = true;
  }
  if (topic == "/replayFinished/" + replaceDotsBySlashes (this->instanceName))
  {
  this->replayFinished = true;
  }
  }
</#if>