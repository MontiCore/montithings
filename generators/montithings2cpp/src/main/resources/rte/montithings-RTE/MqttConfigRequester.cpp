/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include "MqttConfigRequester.h"

MqttConfigRequester::MqttConfigRequester ()
{
  mqttClientInstance = MqttClient::instance ();
  mqttClientInstance->addUser (this);
  mqttClientInstance->subscribe ("/components");
}

void
MqttConfigRequester::requestConfig (std::string instanceName)
{
  std::string instanceNameWithSlashes = replaceDotsBySlashes (instanceName);
  mqttClientInstance->publish ("/prepareComponent", instanceNameWithSlashes);
  answerTopic += "/" + instanceNameWithSlashes;
  mqttClientInstance->subscribe (answerTopic);
  this->instanceName = instanceName;
}

void
MqttConfigRequester::onMessage (mosquitto *mosquitto, void *obj,
                                const struct mosquitto_message *message)
{
  std::string topic = std::string ((char *)message->topic);
  std::string payload = std::string ((char *)message->payload, message->payloadlen);

  if (topic == answerTopic)
    {
      config = json::parse (payload);
      receivedConfig = true;
    }

  // check if this message informs us about new component instances
  if (topic.find ("/components") != std::string::npos)
    {
      // re-request config if our parent just joined
      if (payload == replaceDotsBySlashes (getEnclosingComponentName (instanceName)))
        {
          mqttClientInstance->publish ("/prepareComponent",
                                            replaceDotsBySlashes (instanceName));
        }
    }
}

bool
MqttConfigRequester::hasReceivedConfig () const
{
  return receivedConfig;
}

const json &
MqttConfigRequester::getConfig () const
{
  return config;
}