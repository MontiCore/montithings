/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include "MqttClient.h"
#include "easyloggingpp/easylogging++.h"
#define MQTT_LOG_ID "MQTT_PORT"

MqttClient *
MqttClient::instance (const std::string &brokerHostname, int brokerPort)
{
  static MqttClient *_instance = new MqttClient (brokerHostname, brokerPort);
  return _instance;
}

MqttClient *
MqttClient::localInstance (const std::string &brokerHostname, int brokerPort)
{
  static MqttClient *_instance = new MqttClient (brokerHostname, brokerPort);
  return _instance;
}

MqttClient::MqttClient (const std::string &brokerHostname, int brokerPort)
{
  // Log version number
  int major, minor, revision;
  mosquitto_lib_version (&major, &minor, &revision);
  CLOG (DEBUG, MQTT_LOG_ID) << "Using libmosquitto " << major << "." << minor << "." << revision;

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
    if (MqttClient *currentInstance = static_cast<MqttClient *> (obj))
      {
        currentInstance->onConnect (mosquitto, obj, result);
      }
  });
  mosquitto_disconnect_callback_set (mosq, [] (struct mosquitto *mosquitto, void *obj, int result) {
    if (MqttClient *currentInstance = static_cast<MqttClient *> (obj))
      {
        currentInstance->onDisconnect (mosquitto, obj, result);
      }
  });
  mosquitto_message_callback_set (mosq, [] (struct mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) {
    if (MqttClient *currentInstance = static_cast<MqttClient *> (obj))
      {
        currentInstance->onMessage (mosquitto, obj, message);
      }
  });

  // Connect to MQTT broker
  int keepalive = 60;
  if (mosquitto_connect (mosq, brokerHostname.c_str (), brokerPort, keepalive))
    {
      throw std::runtime_error ("Unable to connect to MQTT broker.");
    }

  _loop = std::async (std::launch::async, &MqttClient::loop, this);
}

MqttClient::~MqttClient ()
{
  mosquitto_destroy (mosq);
  mosquitto_lib_cleanup ();
}

void
MqttClient::publish (const std::string &topic, const std::string &message)
{
  mosquitto_publish (mosq,
                     nullptr, // could be used to set a msg id
                     topic.c_str (), message.length (), message.c_str (), qos, false);
}

void
MqttClient::subscribe (std::string topic)
{
  int returnCode = mosquitto_subscribe (mosq, nullptr, topic.c_str (), qos);
  switch (returnCode)
    {
    case MOSQ_ERR_SUCCESS:
      CLOG (DEBUG, MQTT_LOG_ID) << "Connected to MQTT topic " << topic;
      break;
    case MOSQ_ERR_INVAL:
      CLOG (DEBUG, MQTT_LOG_ID) << "Invalid Input Parameters. Could not connect to MQTT topic "
                                << topic;
      break;
    case MOSQ_ERR_NOMEM:
      CLOG (DEBUG, MQTT_LOG_ID) << "Out of memory. Could not connect to MQTT topic " << topic;
      break;
    case MOSQ_ERR_NO_CONN:
      CLOG (DEBUG, MQTT_LOG_ID) << "No connection to broker. Could not connect to MQTT topic "
                                << topic;
      break;
    case MOSQ_ERR_MALFORMED_UTF8:
      CLOG (DEBUG, MQTT_LOG_ID) << "Topic is not UTF-8. Could not connect to MQTT topic " << topic;
      break;
      /*
      // Not available on Raspberry Pi
      case MOSQ_ERR_OVERSIZE_PACKET:
        CLOG (DEBUG, MQTT_LOG_ID) << "Packet too large. Could not connect to MQTT topic " << topic;
        break;
      */
    }
  MqttClient::instance ()->subscriptions.emplace (topic);
}

void
MqttClient::onConnect (mosquitto *mosquitto, void *obj, int result)
{
  CLOG (DEBUG, MQTT_LOG_ID) << "Connected to MQTT broker " << mosquitto;
  connected = true;

  // (re)subscribe topics
  for (std::string topic : subscriptions)
    {
      subscribe (topic);
    }
}

void
MqttClient::onDisconnect (mosquitto *mosquitto, void *obj, int result)
{
  CLOG (DEBUG, MQTT_LOG_ID) << "Disconnected from MQTT broker";
  connected = false;
}

void
MqttClient::onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message)
{
  for (MqttUser *user : users)
    {
      user->onMessage (mosquitto, obj, message);
    }
}

void
MqttClient::loop ()
{
  mosquitto_loop_forever (mosq, 1, 1);
}

void
MqttClient::wait ()
{
  _loop.wait ();
}

bool
MqttClient::isConnected () const
{
  return connected;
}
