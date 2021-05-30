/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include <map>
#include "easyloggingpp/easylogging++.h"
#include "sole/sole.hpp"

#include "logtracing/interface/LogTracerInterface.h"
#include "MqttUser.h"
#include "MqttClient.h"

class LogTracerMQTTClient : public LogTracerInterface, public MqttUser {
private:
    const std::string TOPIC_REQUEST_PREFIX = "/logtracing/requests/";
    const std::string TOPIC_RESPONSE = "/logtracing/responses/middleware";

    std::function<void(sole::uuid, std::string)> onResponse;
    std::function<void(sole::uuid, sole::uuid, sole::uuid, sole::uuid, Request, long)> onRequest;

public:
    LogTracerMQTTClient(std::string &instanceName, bool shouldSubscribeToResponse);

    ~LogTracerMQTTClient() override = default;

    void response(sole::uuid reqUuid, const std::string &content) override;

    sole::uuid request(std::string instanceName, Request request, time_t fromDatetime) override;

    sole::uuid request(std::string instanceName, Request request, sole::uuid traceUuid) override;

    sole::uuid request(std::string instanceName, Request request, time_t fromDatetime,
                       sole::uuid logUuid, sole::uuid inputUuid,sole::uuid outputUuid) override;

    void addOnResponseCallback(std::function<void(sole::uuid, std::string)> callback) override;

    void addOnRequestCallback(std::function<void(sole::uuid, sole::uuid, sole::uuid, sole::uuid, Request, long)> callback) override;

    void cleanup() override;

    void waitUntilReadersConnected(int number) override;

    void onMessage(mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
};

