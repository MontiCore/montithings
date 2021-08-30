<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{

std::string topic = std::string ((char *)message->topic);
std::string payload = std::string ((char *)message->payload, message->payloadlen);

LOG(DEBUG) << "GOT MESSAGE ON TOPIC " + topic;


<#list comp.getOutgoingPorts() + comp.getIncomingPorts() as p>
    <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign sensorActuatorType = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
        // check if its message from sensorActuatorConfig topic
        if (topic == "/sensorActuator/config/" + ${p.getName()}->getSensorActuatorName()){
            json jsonMessage = json::parse(payload);
            std::string portIdentifier = this->getInstanceName() + ".${p.getName()}";

            if(jsonMessage["occupiedBy"] != portIdentifier && jsonMessage["occupiedBy"] != "False"){
                LOG(DEBUG) << "Topic " + topic + "for sensorActuator ${p.getName()} is taken.";
                mqttClientLocalInstance->unsubscribe (topic);
                exitSignal${p.getName()?cap_first}.set_value();
                th${p.getName()?cap_first}.join();

                // get current index
                LOG(DEBUG) << "sensorActuatorTypes for sink: " << sensorActuatorTypes["${sensorActuatorType}"];
                std::string prefix = "/sensorActuator/config/";
                auto it = std::find(sensorActuatorTypes["${sensorActuatorType}"].begin(), sensorActuatorTypes["${sensorActuatorType}"].end(), topic.substr(prefix.length()));
                int currentIndex = std::distance(sensorActuatorTypes["${sensorActuatorType}"].begin(), it);

                std::string nextTopic;
                if(currentIndex + 1 < sensorActuatorTypes["${sensorActuatorType}"].size()){
                    nextTopic = sensorActuatorTypes["${sensorActuatorType}"][currentIndex+1];
                } else {
                    nextTopic = sensorActuatorTypes["${sensorActuatorType}"][0];
                }

                LOG(DEBUG) << "next topic is: " + nextTopic;

                <#if p.isIncoming()>
                ${p.getName()}->setSensorActuatorName (nextTopic, true);
                <#else>
                ${p.getName()}->setSensorActuatorName (nextTopic, false);
                </#if>
                std::string sensorActuatorConfigTopic = "/sensorActuator/config/" + nextTopic;
                mqttClientLocalInstance->subscribe (sensorActuatorConfigTopic);

                exitSignal${p.getName()?cap_first} = std::promise<void>();
                std::future<void> keepAliveFuture${p.getName()?cap_first} = exitSignal${p.getName()?cap_first}.get_future();
                th${p.getName()?cap_first} = std::thread(&${className}::sendKeepAlive, this, sensorActuatorConfigTopic, "${p.getName()}", std::move(keepAliveFuture${p.getName()?cap_first}));
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

${tc.includeArgs("template.logtracing.hooks.AddInstanceNameToPortRef", [comp, config, "_"])}

}