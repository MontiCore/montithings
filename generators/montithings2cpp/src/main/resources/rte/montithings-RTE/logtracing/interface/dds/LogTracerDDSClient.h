/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include "sole/sole.hpp"

#include "logtracing/interface/LogTracerInterface.h"
#include "logtracing/interface/dds/DDSTracerEntities.h"


class LogTracerDDSClient : public LogTracerInterface, public DDSTracerEntities {
private:
    std::function<void(sole::uuid, std::string)> onResponse;
    std::function<void(sole::uuid, sole::uuid, sole::uuid, sole::uuid, Request, long)> onRequest;

public:
    LogTracerDDSClient(DDSClient &ddsClient,
                       std::string instanceName,
                       bool initReqWriter,
                       bool initReqReader,
                       bool initResWriter,
                       bool initResReader);

    ~LogTracerDDSClient() override = default;

    /**
     * Implementation of the LogTracerInterface
     */
    void response(sole::uuid reqUuid, const std::string &content) override;

    sole::uuid request(std::string instanceName, Request request, time_t fromDatetime) override;

    sole::uuid request(std::string instanceName, Request request, sole::uuid traceUuid) override;

    sole::uuid request(std::string instanceName, Request request, time_t fromDatetime,
                       sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid) override;

    void onResponseCallback(const DDSLogTracerMessage::Response &res);

    void onRequestCallback(const DDSLogTracerMessage::Request &req);

    void addOnResponseCallback(std::function<void(sole::uuid, std::string)> callback) override;

    void addOnRequestCallback(
            std::function<void(sole::uuid, sole::uuid, sole::uuid, sole::uuid, Request, long)> callback) override;


    void waitUntilReadersConnected(int number) override;

};
