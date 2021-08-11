/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "Port.h"
#include <string>
#include <iostream>
#include <utility>
#include <set>
#include "MqttUser.h"
#include <mosquitto.h>
#include <future>

class MqttClient
{
public:
  /// Singleton getter
  static MqttClient *instance (const std::string& brokerHostname = "localhost",
                               int brokerPort = 1883);

  /// Singleton getter for broker on local device
  static MqttClient *localInstance (const std::string& brokerHostname = "localhost",
                                    int brokerPort = 1883);

protected:
  /**
   * Mosquitto instance used to connect to MQTT broker
   */
  struct mosquitto *mosq;

  /**
   * Quality of Service level
   *   QOS 0 – Once (not guaranteed)
   *   QOS 1 – At Least Once (guaranteed)
   *   QOS 2 – Only Once (guaranteed)
   */
  int qos = 2;

  /**
   * All users (i.e. ports) served by this MQTT client
   */
  std::set<MqttUser *> users;

  /**
   * All topics we are subscribed to (or should subscribe to once we connect)
   */
  std::set<std::string> subscriptions;

  /**
   * Endless loop for asynchronously executing MQTT
   */
  std::future<void> _loop;

  /**
   * Tells whether the client is currently connected to the broker
   */
  bool connected = false;

  /**
   * Initializes the port and opens a connection to the MQTT broker
   * \param brokerHostname hostname of the MQTT broker to connect to
   * \param brokerPort port of the broker to connect to
   */
  explicit MqttClient (const std::string &brokerHostname = "localhost",
                       int brokerPort = 1883);

  /**
   * Destroys the MQTT connection
   */
  ~MqttClient ();

public:
  /**
   * Register a user to receive messages from this MQTT client
   * \param user the user to send the messages received by this client to
   */
  void addUser (MqttUser *user)
  {
    users.emplace (user);
  }

  /**
   * Tells whether we are currently connected to a broker
   * \return true if connected to broker
   */
  bool isConnected () const;

  /**
   * Publish the given message on the given port
   * \param topic the topic to publish the message on
   * \param message content of the message to publish
   */
  void publish (const std::string &topic, const std::string &message);

    /**
     * Publish the given message as a retained message on the given port
     * \param topic the topic to publish the message on
     * \param message content of the message to publish
     */
    void publishRetainedMessage (const std::string &topic, const std::string &message);

  /**
   * Subscribe to the messages of the port with the provided name
   * \param topic fully qualified name of the port to subscribe to
   */
  void subscribe (std::string topic);

  /**
   * Network loop from Mosquitto. We need to start this for Mosquitto to work
   */
  void loop ();

  /**
   * Waits for the Mosquitto loop to finish. If there are no runtime exceptions,
   * the Mosquitto loop is infinite. Therefore, this can be used to keep the main
   * thread of the program alive.
   */
  void wait();

  /* ============================================================ */
  /* ======================= MQTT Callbacks ===================== */
  /* ============================================================ */

  /**
   * Callback that is called when a connection is established, i.e. this is
   * called when the broker sends a CONNACK message in response to a connection
   *
   * \see https://mosquitto.org/api/files/mosquitto-h.html#mosquitto_connect_callback_set
   * \param mosquitto the mosquitto instance making the callback
   * \param obj the user data provided in mosquitto_new
   * \param result the return code of the connection response, one of:
   *               0 - success
   *               1 - connection refused (unacceptable protocol version)
   *               2 - connection refused (identifier rejected)
   *               3 - connection refused (broker unavailable)
   *               4-255 - reserved for future use
   */
  void onConnect (mosquitto *mosquitto, void *obj, int result);

  /**
   * Callback that is called when the client gets disconnected, i.e. this is
   * called when the broker has received the DISCONNECT command and has
   * disconnected the client
   *
   * \see https://mosquitto.org/api/files/mosquitto-h.html#mosquitto_disconnect_callback_set
   * \param the mosquitto instance making the callback.
   * \param obj	the user data provided in mosquitto_new
   * \param rc	integer value indicating the reason for the disconnect.
   *            A value of 0 means the client has called mosquitto_disconnect.
   *            Any other value indicates that the disconnect is unexpected.
   */
  void onDisconnect (mosquitto *mosquitto, void *obj, int result);

  /**
   * Callback that is called when a message is received from the broker.
   *
   * \see https://mosquitto.org/api/files/mosquitto-h.html#mosquitto_message_callback_set
   * \param mosquitto the mosquitto instance making the callback.
   * \param obj the user data provided in mosquitto_new
   * \param message the message data. This variable and associated memory will
   *                be freed by the library after the callback completes. The
   *                client should make copies of any of the data it requires.
   */
  void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message);
};

