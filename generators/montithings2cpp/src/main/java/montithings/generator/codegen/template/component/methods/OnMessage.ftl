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
                //exitSignal${p.getName()?cap_first}.set_value();
                //th${p.getName()?cap_first}.join();

                std::string newTopic = jsonMessage["topic"];
                <#if p.isIncoming()>
                ${p.getName()}->setSensorActuatorName (newTopic, true);
                <#else>
                ${p.getName()}->setSensorActuatorName (newTopic, false);
                </#if>
                mqttClientLocalInstance->subscribe ("/sensorActuator/data/" + newTopic);
                //exitSignal${p.getName()?cap_first} = std::promise<void>();
                //std::future<void> keepAliveFuture${p.getName()?cap_first} = exitSignal${p.getName()?cap_first}.get_future();
                //th${p.getName()?cap_first} = std::thread(&${className}::sendKeepAlive, this, "/sensorActuator/heartbeat/" + newTopic, "${p.getName()}", "${sensorActuatorType}", std::move(keepAliveFuture${p.getName()?cap_first}));
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