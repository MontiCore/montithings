/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include "UniqueElement.h"
#include "Utils.h"
#include "easyloggingpp/easylogging++.h"
#include "json/json.hpp"
#include "Message.h"
#include <future>
#include <mosquitto.h>
#include <string>
#include <utility>

using nlohmann::json;

/**
 * MontiThingsConnector is used by external ports to communicate with MontiThings.
 * Incoming ports (from MontiThings' point of view) call the MontiThingsConnector::send method
 * to provide values to MontiThings.
 * Outgoing ports (from MontiThings' point of view) implement and set the
 * MontiThingsConnector::onReceive callback. In this callback the external port can handle
 * the message provided by MontiThings.
 *
 * @tparam T Data type of the messages exchanged by the port
 */
template <typename T> class MontiThingsConnector : public UniqueElement
{
protected:
  /// Hostname (or IP) of the MQTT broker that manages the sensors
  const std::string brokerHostname = "localhost";

  /// Port (in the networking sense) of the MQTT broker
  const int brokerPort = 1883;

  /// Type of the port (e.g. "int")
  const std::string portType;

  /// True, iff the connector is connected to MQTT broker
  bool isConnected = false;

  /// Mosquitto instance used to connect to MQTT broker
  struct mosquitto *mosq;

  /// Callback for messages to be processed, received from MontiThings components
  void (*onReceive) (T) = nullptr;

  /// Endless loop for asynchronously executing MQTT
  std::future<void> _loop;

  /**
   * Publish the given message on the given port
   * \param topic the topic to publish the message on
   * \param message content of the message to publish
   */
  void publish (const std::string &topic, const std::string &message);

  /**
   * Subscribe to the messages of the port with the provided name
   * \param topic fully qualified name of the port to subscribe to
   */
  void subscribe (const std::string &topic);

public:
  MontiThingsConnector (std::string brokerHostname, int brokerPort, std::string portType,
                        void (*onReceive) (T));
  MontiThingsConnector (std::string portType, void (*onReceive) (T));

  /// Connects to MQTT broker
  void connectToBroker ();

  /**
   * Method that sends a given value to the connected MontiThings component.
   * This method is called by connectors of incoming ports.
   * It automatically serializes and wraps the values.
   *
   * \param value the raw value to be sent to MontiThings
   */
  void send (T value);

  /**
   * Network loop from Mosquitto. We need to start this for Mosquitto to work
   */
  void loop ();

  /**
   * Waits for the Mosquitto loop to finish. If there are no runtime exceptions,
   * the Mosquitto loop is infinite. Therefore, this can be used to keep the main
   * thread of the program alive.
   */
  void wait ();

  /* ============================================================ */
  /* ======================= MQTT Callbacks ===================== */
  /* ============================================================ */
protected:
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

template <typename T>
MontiThingsConnector<T>::MontiThingsConnector (std::string brokerHostname, const int brokerPort,
                                               std::string portType, void (*onReceive) (T))
    : brokerHostname (std::move (brokerHostname)), brokerPort (brokerPort),
      portType (std::move (portType)), onReceive (onReceive)
{
  connectToBroker ();
}

template <typename T>
MontiThingsConnector<T>::MontiThingsConnector (std::string portType, void (*onReceive) (T))
    : portType (std::move (portType)), onReceive (onReceive)
{
  connectToBroker ();
}

template <typename T>
void
MontiThingsConnector<T>::connectToBroker ()
{
  // Log version number
  int major, minor, revision;
  mosquitto_lib_version (&major, &minor, &revision);
  LOG (DEBUG) << "Using libmosquitto " << major << "." << minor << "." << revision;

  // Initialize
  mosquitto_lib_init ();

  // Create instance
  mosq = mosquitto_new (nullptr, true, this);
  if (!mosq)
    {
      throw std::runtime_error ("Failed to initialize libmosquitto");
    }

  // Set callbacks
  mosquitto_connect_callback_set (mosq, [] (struct mosquitto *mosquitto, void *obj, int result) {
    auto *mtc = static_cast<MontiThingsConnector<T> *> (obj);
    mtc->onConnect (mosquitto, obj, result);
  });
  mosquitto_disconnect_callback_set (mosq, [] (struct mosquitto *mosquitto, void *obj, int result) {
    auto *mtc = static_cast<MontiThingsConnector<T> *> (obj);
    mtc->onDisconnect (mosquitto, obj, result);
  });
  mosquitto_message_callback_set (mosq, [] (struct mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) {
    auto *mtc = static_cast<MontiThingsConnector<T> *> (obj);
    mtc->onMessage (mosquitto, obj, message);
  });

  // Connect to MQTT broker
  int keepalive = 60;
  if (mosquitto_connect (mosq, brokerHostname.c_str (), brokerPort, keepalive))
    {
      throw std::runtime_error ("Unable to connect to MQTT broker.");
    }

  json topicSpecification;
  topicSpecification["topic"] = this->uuid.str ();
  topicSpecification["spec"]["type"] = this->portType;

  publish ("/sensorActuator/offer/" + this->getUuid ().str (), topicSpecification.dump ());
  subscribe ("/sensorActuator/data/" + this->uuid.str ());

  _loop = std::async (std::launch::async, &MontiThingsConnector<T>::loop, this);
}

template <typename T>
void
MontiThingsConnector<T>::send (T value)
{
  std::string topic = "/sensorActuator/data/" + this->uuid.str ();
  std::string payload = dataToJson (Message<T> (value));
  publish (topic, payload);
}

template <typename T>
void
MontiThingsConnector<T>::loop ()
{
  mosquitto_loop_forever (mosq, 1, 1);
}

template <typename T>
void
MontiThingsConnector<T>::wait ()
{
  _loop.wait ();
}

template <typename T>
void
MontiThingsConnector<T>::onConnect (mosquitto *mosquitto, void *obj, int result)
{
  LOG (DEBUG) << "Connected to MQTT broker " << mosquitto;
  isConnected = true;
}

template <typename T>
void
MontiThingsConnector<T>::onDisconnect (mosquitto *mosquitto, void *obj, int result)
{
  LOG (DEBUG) << "Disconnected from MQTT broker " << mosquitto;
  isConnected = false;
}

template <typename T>
void
MontiThingsConnector<T>::onMessage (mosquitto *mosquitto, void *obj,
                                    const struct mosquitto_message *message)
{
  std::string topic = std::string ((char *)message->topic);
  std::string payload = std::string ((char *)message->payload, message->payloadlen);
  try
    {
      Message<T> result = jsonToData<Message<T>> (payload);
      LOG (DEBUG) << "Received '" << payload << "'";
      if (this->onReceive != nullptr)
        {
          this->onReceive (result.getPayload().value());
        }
    }
  catch (...)
    {
      LOG (WARNING) << "Could not parse incoming message '" << payload << "'";
    }
}

template <typename T>
void
MontiThingsConnector<T>::publish (const std::string &topic, const std::string &message)
{
  mosquitto_publish (mosq,
                     nullptr, // could be used to set a msg id
                     topic.c_str (), message.length (), message.c_str (), 2, false);
}

template <typename T>
void
MontiThingsConnector<T>::subscribe (const std::string &topic)
{
  int returnCode = mosquitto_subscribe (mosq, nullptr, topic.c_str (), 2);
  switch (returnCode)
    {
    case MOSQ_ERR_SUCCESS:
      LOG (DEBUG) << "Connected to MQTT topic " << topic;
      break;
    case MOSQ_ERR_INVAL:
      LOG (DEBUG) << "Invalid Input Parameters. Could not connect to MQTT topic " << topic;
      break;
    case MOSQ_ERR_NOMEM:
      LOG (DEBUG) << "Out of memory. Could not connect to MQTT topic " << topic;
      break;
    case MOSQ_ERR_NO_CONN:
      LOG (DEBUG) << "No connection to broker. Could not connect to MQTT topic " << topic;
      break;
    case MOSQ_ERR_MALFORMED_UTF8:
      LOG (DEBUG) << "Topic is not UTF-8. Could not connect to MQTT topic " << topic;
      break;
      /*
      // Not available on Raspberry Pi
      case MOSQ_ERR_OVERSIZE_PACKET:
        LOG (DEBUG) << "Packet too large. Could not connect to MQTT topic " << topic;
        break;
      */
    }
}
