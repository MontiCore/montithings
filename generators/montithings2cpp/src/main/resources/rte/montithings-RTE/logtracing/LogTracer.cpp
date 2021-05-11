/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogTracer.h"

#include <utility>

namespace montithings {
    LogTracer::LogTracer(std::string instanceName, LogTracerInterface &interface)
        : instanceName(std::move(instanceName)), interface(&interface) {
        currInputId = uuid();
        currOutputId = uuid();
        montithings::subscribeLogTracer(this);

        interface.addOnRequestCallback(std::bind(&LogTracer::onRequest, this, std::placeholders::_1,
                                                 std::placeholders::_2,
                                                 std::placeholders::_3,
                                                 std::placeholders::_4));
    }

    sole::uuid
    LogTracer::uuid() {
        return sole::uuid4();
    }

    void
    LogTracer::handleLogEntry(const std::string &content) {
        sole::uuid id = uuid();
        logEntries.insert({id, LogEntry(time(nullptr), content)});
        currInputLogs.push_back(id);
    }

    sole::uuid
    LogTracer::newOutput() {
        allOutputLogs[currOutputId] = currOutputLogs;
        currOutputLogs.clear();
        currOutputId = uuid();

        return currOutputId;
    }

    LogTracerInterface *LogTracer::getInterface() const {
        return interface;
    }

    void
    LogTracer::onRequest(sole::uuid reqUuid, sole::uuid traceUuid, LogTracerInterface::Request reqType, long fromTimestamp) {
        std::cout << "got something!" << std::endl;
    }

}