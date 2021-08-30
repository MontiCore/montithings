/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "MqttClient.h"
#include "MqttUser.h"
#include "Utils.h"
#include "json/json.hpp"

using json = nlohmann::json;

/**
 * Can be used to receive a configuration for a component instance
 * from the system
 */
class MqttConfigRequester : public MqttUser
{
  /// true if a configuration has been received
  bool receivedConfig = false;

  /// the received configuration
  json config;

  /// top level topic on which configurations are published
  std::string answerTopic = "/config";

  /// all instance names for which a configuration has been requested
  std::string instanceName;

  MqttClient *  mqttClientInstance;


public:
  MqttConfigRequester ();

  /**
   * Request a configuration from the system (which will be provided by the enclosing component)
   * \param instanceName fully qualified instance name of the component instance making the request
   */
  void requestConfig (std::string instanceName);

  /**
   * Callback for receiving MQTT messages
   */
  void onMessage (mosquitto *mosquitto, void *obj,
                  const struct mosquitto_message *message) override;

  /**
   * \return True if a configuration has been received
   */
  bool hasReceivedConfig () const;

  /**
   * \return the received configuration
   */
  const json &getConfig () const;
};
