/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "../../../sole/sole.hpp"

#include "logtracing/interface/LogTracerInterface.h"
#include "logtracing/interface/dds/DDSEntities.h"

class LogTracerDDSClient : public LogTracerInterface, public DDSEntities {
private:
    std::function<void(sole::uuid, std::string)> onResponse;
    std::function<void(sole::uuid, sole::uuid, Request, long)> onRequest;

public:
    LogTracerDDSClient(int argc, char *argv[],
                       bool initReqWriter,
                       bool initReqReader,
                       bool initResWriter,
                       bool initResReader);

    ~LogTracerDDSClient() override = default;

    void response(sole::uuid reqUuid, const std::string &content) override;

    void request(std::string instanceName, Request request, time_t fromDatetime) override;

    void request(std::string instanceName, Request request, time_t fromDatetime, sole::uuid traceUuid) override;

    void onResponseCallback(const DDSLogTracerMessage::Response &res);

    void onRequestCallback(const DDSLogTracerMessage::Request &req);

    void addOnResponseCallback(std::function<void(sole::uuid, std::string)> callback) override;

    void addOnRequestCallback(std::function<void(sole::uuid, sole::uuid, Request, long)> callback) override;

    void cleanup() override;

    void waitUntilReadersConnected(int number) override;

};
