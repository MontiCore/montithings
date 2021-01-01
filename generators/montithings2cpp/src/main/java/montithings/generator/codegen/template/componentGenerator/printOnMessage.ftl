<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

void
${className}::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{
std::string topic = std::string ((char *) message->topic);
std::string payload = std::string ((char *) message->payload, message->payloadlen);

// check if this message informs us about new component instances
if (topic.find ("/components") != std::string::npos)
{
// check if its about one of our subcomponents
// (whose fully qualified name starts with our instance name)
if (payload.find (replaceDotsBySlashes(instanceName)) != std::string::npos)
{
// inform the new component about the connectors
publishConnectors();
}
}
}