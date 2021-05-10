<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

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