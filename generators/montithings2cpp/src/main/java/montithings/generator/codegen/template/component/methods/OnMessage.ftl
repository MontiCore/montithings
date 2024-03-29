<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{

std::string topic = std::string ((char *)message->topic);
std::string payload = std::string ((char *)message->payload, message->payloadlen);

<#list comp.getOutgoingPorts() + comp.getIncomingPorts() as p>
    <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign sensorActuatorType = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
        std::string ${p.getName()}PortIdentifier = this->getInstanceName() + ".${p.getName()}";
        // check if its message from sensorActuatorResponse topic
        if (topic == "/sensorActuator/response/" + ${p.getName()}PortIdentifier){
            json jsonMessage = json::parse(payload);

            if(jsonMessage.find("topic") != jsonMessage.end()){
                currentTopic${p.getName()?cap_first} = jsonMessage["topic"];
                <#if p.isIncoming()>
                ${p.getName()}->setSensorActuatorName (currentTopic${p.getName()?cap_first}, true);
                <#else>
                ${p.getName()}->setSensorActuatorName (currentTopic${p.getName()?cap_first}, false);
                </#if>
                mqttClientLocalInstance->subscribe ("/sensorActuator/heartbeat/" + currentTopic${p.getName()?cap_first});
                exitSignal${p.getName()?cap_first} = std::promise<void>();
                std::future<void> keepAliveFuture${p.getName()?cap_first} = exitSignal${p.getName()?cap_first}.get_future();
                th${p.getName()?cap_first} = std::thread(&${className}::sendKeepAlive, this, "/sensorActuator/heartbeat/" + currentTopic${p.getName()?cap_first}, "${p.getName()}", "${sensorActuatorType}", std::move(keepAliveFuture${p.getName()?cap_first}));
            }
        }
        else if (topic == ("/sensorActuator/heartbeat/" + currentTopic${p.getName()?cap_first})) {
            json jsonMessage = json::parse(payload);
            if (jsonMessage["occupiedBy"] != ${p.getName()}PortIdentifier && jsonMessage["occupiedBy"] != "False") {
                mqttClientLocalInstance->unsubscribe("/sensorActuator/data/" + currentTopic${p.getName()?cap_first});
                mqttClientLocalInstance->unsubscribe("/sensorActuator/heartbeat/" + currentTopic${p.getName()?cap_first});
                exitSignal${p.getName()?cap_first}.set_value();
                th${p.getName()?cap_first}.join();

                std::string sensorActuatorRequestTopic${p.getName()?cap_first} = "/sensorActuator/request/" + this->getInstanceName() + ".${p.getName()}";
                mqttClientLocalInstance->subscribe ("/sensorActuator/response/" + this->getInstanceName() + ".${p.getName()}");
                mqttClientLocalInstance->publishRetainedMessage (sensorActuatorRequestTopic${p.getName()?cap_first}, "{\"type\":\"${sensorActuatorType}\"}");
            }
        }
    </#if>
</#list>

// check if its about one of our subcomponents
// (whose fully qualified name starts with our instance name)
bool refersToSubcomp = payload.find (replaceDotsBySlashes (instanceName)) != std::string::npos;

// check if this message informs us about new component
// instance that requires information from us
if (topic == "/prepareComponent")
{
if (refersToSubcomp)
{
// inform the new component about its parameters
publishConfigForSubcomponent (replaceDotsBySlashes(payload));
}
}

// check if this message informs us about new component instances
else if (topic.find ("/components") != std::string::npos)
{
if (refersToSubcomp)
{
// inform the new component about the connectors
publishConnectors ();
}
}

<#if ComponentHelper.shouldGenerateCompatibilityHeartbeat(comp, config)>
  <#if ComponentHelper.getPortsWithTestBlocks(comp)?size <= 0>
    <#list ComponentHelper.getInterfaceClassNames(comp)[0..*1] as interface>
      else if (topic.find ("/offered_ip/${interface}") != std::string::npos) {
        if (payload != ip_address) {
          mqttClientSenderInstance = new MqttClient(payload, 1883);
          mqttClientSenderInstanceHasBeenConnected = true;
          mqttClientInstance->subscribe("/new-subscriptions/${interface}");
          mqttClientInstance->subscribe("/connection-start/${interface}");
        }
      }
      else if (topic.find("/new-subscriptions/${interface}") != std::string::npos) {
        mqttClientInstance->subscribe("/ports/" + payload);
        subscriptionsToSend.emplace("/ports/" + payload);
      }
      else if (topic.find("/connection-start/${interface}") != std::string::npos) {
        if (payload == "success") {
          isConnectedToOtherComponent = true;
        } else {
          subscriptionsToSend.clear();
        }
      }
      else if (subscriptionsToSend.find(topic) != subscriptionsToSend.cend() && mqttClientSenderInstance->isConnected()) {
        mqttClientSenderInstance->publish(topic, payload);
      }
    </#list>
  <#else>
    <#list ComponentHelper.getPortsWithTestBlocks(comp) as p>
      // check if this message informs us about a new component match
      else if (topic.find ("/component_match/${p.getType().print()}") != std::string::npos) {
        LOG(DEBUG) << "Component Match message received!";
        mqttClientInstance->publish("/portsInject/" + replaceDotsBySlashes ("${p.getFullName()}"), payload);
      }
      else if (topic.find ("/offered_ip/${p.getType().print()}") != std::string::npos) {
        if (payload != ip_address) {
          mqttClientSenderInstance${p.getName()} = new MqttClient(payload, 1883);
          mqttClientSenderInstance${p.getName()}HasBeenConnected = true;
        }
      }
      else if (subscriptionsToSend${p.getName()}.find(topic) != subscriptionsToSend${p.getName()}.cend() && mqttClientSenderInstance${p.getName()}->isConnected()) {
        mqttClientSenderInstance${p.getName()}->publish(topic, payload);
      }
    </#list>
  </#if>
</#if>
<#if ComponentHelper.isDSLComponent(comp,config)>
// receive python files
if (topic == "/hwc/" + replaceDotsBySlashes(instanceName)){
  python_receiver(payload);
}
</#if>

${tc.includeArgs("template.logtracing.hooks.AddInstanceNameToPortRef", [comp, config, "_"])}

}