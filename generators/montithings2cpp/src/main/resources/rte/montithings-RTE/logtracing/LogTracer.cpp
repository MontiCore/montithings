/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogTracer.h"

#include <utility>

namespace montithings {

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

    LogTracer::LogTracer(std::string instanceName) : instanceName(std::move(instanceName)) {
        currInputId = uuid();
        currOutputId = uuid();
        montithings::subscribe(this);
    }


}