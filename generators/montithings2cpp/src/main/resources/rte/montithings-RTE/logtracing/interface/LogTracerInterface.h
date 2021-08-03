/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

/**
 * Interface used to communicate with the middleware which is implemented by corresponding DDS and MQTT clients.
 */

#pragma once

#include <string>
#include "sole/sole.hpp"

class LogTracerInterface {
public:
    // A request asks for specific data which can be differentiated as follows
    enum Request {
        // log entries are requested
        LOG_ENTRIES, 
        // internal data, hence variable assignments, traces, 
        // and serialized inputs related to a log entry is requested
        INTERNAL_DATA,
        // internal data again, but this time related to a specific trace and not a log entry
        TRACE_DATA
    };

    virtual ~LogTracerInterface() = default;

    // a response always refers to a specific request ID because request-and-response is 
    // realized on top of an asynchronous publish-subscribe pattern and the messages have to be matched somehow
    virtual void response(sole::uuid reqUuid, const std::string &content) = 0;

    // for convenience the request method is overloaded, depending on what type of request is used.
    // used for LOG_ENTRIES requests
    virtual sole::uuid request(std::string instanceName, Request request, time_t fromDatetime) = 0;

    // used for TRACE_DATA requests
    virtual sole::uuid request(std::string instanceName, Request request, sole::uuid traceUuid) = 0;

    // used for INTERNAL_DATA requests
    virtual sole::uuid request(std::string instanceName, Request request, time_t fromDatetime,
                               sole::uuid logUuid, sole::uuid inputUuid,sole::uuid outputUuid) = 0;

    virtual void addOnResponseCallback(std::function<void(sole::uuid reqUuid, std::string content)> callback) = 0;

    virtual void addOnRequestCallback(std::function<void(sole::uuid reqUuid, sole::uuid logUuid,
                                                         sole::uuid inputUuid, sole::uuid outUuid, 
                                                         Request reqType, long from_timestamp)> callback) = 0;

    // Depending on the used transport, its helpful to be able to wait until at least a specific number of 
    // component instances are listening for requests.
    // Is used by the middleware in case of DDS is enabled 
    virtual void waitUntilReadersConnected(int number) = 0;
};
