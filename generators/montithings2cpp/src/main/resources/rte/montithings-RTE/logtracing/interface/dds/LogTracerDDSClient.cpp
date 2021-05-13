/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogTracerDDSClient.h"

void LogTracerDDSClient::response(sole::uuid reqUuid, const std::string &content) {
    std::string topic = "/response";

    DDSLogTracerMessage::Response res;
    res.req_uuid = reqUuid.str().c_str();
    res.content = content.c_str();

    send(res);
}

LogTracerDDSClient::LogTracerDDSClient(int argc, char *argv[],
                                       bool initReqWriter,
                                       bool initReqReader,
                                       bool initResWriter,
                                       bool initResReader) {
    initParticipant(argc, argv);
    initPublisher();
    initSubscriber();
    initMessageType();
    initTopic();

    if (initReqWriter) {
        initRequestDataWriter();
    }

    if (initReqReader) {
        initRequestDataReader();
        addRequestCallback(std::bind(&LogTracerDDSClient::onRequestCallback, this, std::placeholders::_1));
    }

    if (initResWriter) {
        initResponseDataWriter();
    }

    if (initResReader) {
        initResponseDataReader();
        addResponseCallback(std::bind(&LogTracerDDSClient::onResponseCallback, this, std::placeholders::_1));
    }
}

void
LogTracerDDSClient::request(std::string instanceName, LogTracerInterface::Request requestData, time_t fromDatetime) {
    request(instanceName, requestData, fromDatetime, sole::uuid4());
}

void LogTracerDDSClient::request(std::string instanceName, LogTracerInterface::Request requestData, time_t fromDatetime,
                                 sole::uuid traceUuid) {
    std::string topic = "/request/" + instanceName;

    DDSLogTracerMessage::Request req;
    req.from_timestamp = (long) fromDatetime;
    req.req_uuid = sole::uuid4().str().c_str();

    if (requestData == LogTracerInterface::INTERNAL_DATA) {
        req.req_data = DDSLogTracerMessage::INTERNAL_DATA;
        req.trace_uuid = traceUuid.str().c_str();
    } else {
        req.req_data = DDSLogTracerMessage::LOG_ENTRIES;
    }

    send(req);
}

void
LogTracerDDSClient::onResponseCallback(const DDSLogTracerMessage::Response &res) {
    if(onResponse) {
        onResponse(sole::rebuild(res.req_uuid.in()), res.content.in());
    }
}

void
LogTracerDDSClient::onRequestCallback(const DDSLogTracerMessage::Request &res) {
    sole::uuid reqUuid = sole::rebuild(res.req_uuid.in());
    sole::uuid traceUuid = sole::rebuild(res.trace_uuid.in());

    LogTracerInterface::Request reqType = LogTracerInterface::LOG_ENTRIES;

    if (res.req_data == DDSLogTracerMessage::INTERNAL_DATA) {
        reqType = LogTracerInterface::INTERNAL_DATA;
    }

    if(onRequest) {
        onRequest(reqUuid, traceUuid, reqType, res.from_timestamp);
    }
}

void LogTracerDDSClient::addOnResponseCallback(std::function<void(sole::uuid, std::string)> callback) {
    onResponse = std::move(callback);
}

void LogTracerDDSClient::addOnRequestCallback(std::function<void(sole::uuid, sole::uuid, Request, long)> callback) {
    onRequest = std::move(callback);
}

void LogTracerDDSClient::cleanup() {
    DDSEntities::cleanup();
}

void LogTracerDDSClient::waitUntilReadersConnected(int number) {
    DDSEntities::waitUntilReadersConnected(number);
}