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

LogTracerDDSClient::LogTracerDDSClient(DDSClient &client,
                                       std::string instanceName,
                                       bool initReqWriter,
                                       bool initReqReader,
                                       bool initResWriter,
                                       bool initResReader)  {
    ddsClient = &client;
    setInstanceName(instanceName);
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

sole::uuid
LogTracerDDSClient::request(std::string instanceName, LogTracerInterface::Request requestData, time_t fromDatetime) {
    return request(instanceName, requestData, fromDatetime, sole::uuid4(), sole::uuid4(), sole::uuid4());
}

sole::uuid
LogTracerDDSClient::request(std::string instanceName, LogTracerInterface::Request requestData, sole::uuid traceUuid) {
    return request(instanceName, requestData, time(nullptr), traceUuid, sole::uuid4(), sole::uuid4());
}

sole::uuid
LogTracerDDSClient::request(std::string instanceName, LogTracerInterface::Request requestData, time_t fromDatetime,
                            sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid) {
    sole::uuid reqUuid = sole::uuid4();

    DDSLogTracerMessage::Request req;
    req.target_instance = instanceName.c_str();
    req.from_timestamp = (long) fromDatetime;
    req.req_uuid = reqUuid.str().c_str();

    if (requestData == LogTracerInterface::INTERNAL_DATA) {
        req.req_data = DDSLogTracerMessage::INTERNAL_DATA;
        req.log_uuid = logUuid.str().c_str();
        req.input_uuid = inputUuid.str().c_str();
        req.output_uuid = outputUuid.str().c_str();
    } else if (requestData == LogTracerInterface::LOG_ENTRIES) {
        req.req_data = DDSLogTracerMessage::LOG_ENTRIES;
    } else {
        req.log_uuid = logUuid.str().c_str();
        req.req_data = DDSLogTracerMessage::TRACE_DATA;
    }

    send(req);
    return reqUuid;
}

void
LogTracerDDSClient::onResponseCallback(const DDSLogTracerMessage::Response &res) {
    if (onResponse) {
        onResponse(sole::rebuild(res.req_uuid.in()), res.content.in());
    }
}

void
LogTracerDDSClient::onRequestCallback(const DDSLogTracerMessage::Request &res) {
    sole::uuid reqUuid = sole::rebuild(res.req_uuid.in());
    sole::uuid logUuid = sole::rebuild(res.log_uuid.in());
    sole::uuid inputUuid = sole::rebuild(res.input_uuid.in());
    sole::uuid outputUuid = sole::rebuild(res.output_uuid.in());

    LogTracerInterface::Request reqType = LogTracerInterface::LOG_ENTRIES;

    if (res.req_data == DDSLogTracerMessage::INTERNAL_DATA) {
        reqType = LogTracerInterface::INTERNAL_DATA;
    } else if (res.req_data == DDSLogTracerMessage::LOG_ENTRIES) {
        reqType = LogTracerInterface::LOG_ENTRIES;
    } else {
        reqType = LogTracerInterface::TRACE_DATA;
    }

    if (onRequest) {
        onRequest(reqUuid, logUuid, inputUuid, outputUuid, reqType, res.from_timestamp);
    }
}

void LogTracerDDSClient::addOnResponseCallback(std::function<void(sole::uuid, std::string)> callback) {
    onResponse = std::move(callback);
}

void LogTracerDDSClient::addOnRequestCallback(std::function<void(sole::uuid, sole::uuid, sole::uuid, sole::uuid, Request, long)> callback) {
    onRequest = std::move(callback);
}


void LogTracerDDSClient::waitUntilReadersConnected(int number) {
    DDSTracerEntities::waitUntilReadersConnected(number);
}