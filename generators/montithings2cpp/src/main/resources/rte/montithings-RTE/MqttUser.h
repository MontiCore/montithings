/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include <mosquitto.h>

class MqttUser
{
  public:
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
  virtual void
  onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) = 0;
};