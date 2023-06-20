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
if (topic.find ("/components") != std::string::npos)
{
if (refersToSubcomp)
{
// inform the new component about the connectors
publishConnectors ();
}
}

<#if ComponentHelper.getIncomingPortsToTest(comp)?size gt 0>
  <#list comp.getAllIncomingPorts()[0..*1] as p>
    // check if this message informs us about a new component match
    if (topic.find ("/component_match") != std::string::npos) {
      LOG(DEBUG) << "Component Match message received!";
      mqttClientInstance->publish("/portsInject/" + replaceDotsBySlashes ("${p.getFullName()}"), payload);
    }
  </#list>
</#if>

${tc.includeArgs("template.logtracing.hooks.AddInstanceNameToPortRef", [comp, config, "_"])}

}