/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore


#include <Utils.h>
#include <logtracing/interface/mqtt/message-types/DataModels.h>
#include "LogTracerMQTTClient.h"

void LogTracerMQTTClient::response(sole::uuid reqUuid, const std::string &content) {
    ResponseStruct res = {};
    res.req_uuid = reqUuid;
    res.content = content;

    MqttClient::instance()->publish(TOPIC_RESPONSE, dataToJson(res));
}

sole::uuid
LogTracerMQTTClient::request(std::string instanceName, LogTracerInterface::Request requestType, time_t fromDatetime) {
    return request(instanceName, requestType, fromDatetime, sole::uuid4(), sole::uuid4(), sole::uuid4());
}

sole::uuid
LogTracerMQTTClient::request(std::string instanceName, LogTracerInterface::Request requestType, sole::uuid traceUuid) {
    return request(instanceName, requestType, time(nullptr), traceUuid, sole::uuid4(), sole::uuid4());
}

sole::uuid
LogTracerMQTTClient::request(std::string instanceName, LogTracerInterface::Request requestType, time_t fromDatetime,
                             sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid) {
    sole::uuid reqUuid = sole::uuid4();

    RequestStruct req = {
            reqUuid,  // req_uuid
            logUuid,  // log_uuid
            inputUuid,  // input_uuid
            outputUuid,  // output_uuid
            instanceName,
            (long) fromDatetime,  // from_timestamp
            RequestType::LOG_ENTRIES // req_type
    };
    if (requestType == LogTracerInterface::INTERNAL_DATA) {
        req.req_type = RequestType::INTERNAL_DATA;
    } else if (requestType == LogTracerInterface::LOG_ENTRIES) {
        req.req_type = RequestType::LOG_ENTRIES;
    } else {
        req.req_type = RequestType::TRACE_DATA;
    }

    std::string topicRequest = TOPIC_REQUEST_PREFIX + replaceDotsBySlashes (instanceName);
    MqttClient::instance()->publish(topicRequest, dataToJson(req));

    return reqUuid;
}

void LogTracerMQTTClient::addOnResponseCallback(std::function<void(sole::uuid, std::string)> callback) {
    onResponse = std::move(callback);
}

void LogTracerMQTTClient::addOnRequestCallback(
        std::function<void(sole::uuid, sole::uuid, sole::uuid, sole::uuid, Request, long)> callback) {
    onRequest = std::move(callback);
}

void LogTracerMQTTClient::waitUntilReadersConnected(int number) {
    // intentionally left empty; is used by DDS
}

LogTracerMQTTClient::LogTracerMQTTClient(std::string &instanceName, bool shouldSubscribeToResponse) {
    MqttClient::instance ()->addUser (this);

    std::string topicRequest = TOPIC_REQUEST_PREFIX + replaceDotsBySlashes (instanceName);
    MqttClient::instance ()->subscribe (topicRequest);

    if(shouldSubscribeToResponse) {
        std::string topicResponse = TOPIC_RESPONSE;
        MqttClient::instance ()->subscribe (topicResponse);
    }
}

void LogTracerMQTTClient::onMessage(mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) {
    std::string topic = std::string ((char *)message->topic);
    std::string payload = std::string ((char *)message->payload, message->payloadlen);

    try {
        RequestStruct req = jsonToData<RequestStruct>(payload);
        if (onRequest) {
            LogTracerInterface::Request reqType = Request::LOG_ENTRIES;
            if (req.req_type == RequestType::TRACE_DATA) {
                reqType = Request::TRACE_DATA;
            } else if (req.req_type == RequestType::INTERNAL_DATA) {
                reqType = Request::INTERNAL_DATA;
            }

            onRequest(req.req_uuid,
                      req.log_uuid,
                      req.input_uuid,
                      req.output_uuid,
                      reqType,
                      req.from_timestamp);
        }
    }
    catch (...) { /* intentionally left empty */ }

    try {
        ResponseStruct res = jsonToData<ResponseStruct>(payload);

        if (onResponse) {
            onResponse(res.req_uuid, res.content);
        }
    }
    catch (...) { /* intentionally left empty */ }

}
