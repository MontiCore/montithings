<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{

std::string topic = std::string ((char *)message->topic);
std::string payload = std::string ((char *)message->payload, message->payloadlen);

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

}