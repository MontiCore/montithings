/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include <map>
#include "sole/sole.hpp"
#include "LogEntry.h"

namespace montithings {
class TraceInput {
private:
    sole::uuid uuid{};
    time_t arrival_time;
    std::vector<LogEntry> logEntries;
    sole::uuid outputRef{};
    std::string serializedInput;

    std::multimap<sole::uuid, std::string> traceIdsWithPortNames;

public:
    TraceInput()  {
        arrival_time = time(nullptr);
        uuid = sole::uuid4();
    }

    virtual ~TraceInput() = default;

    const sole::uuid &getUuid() const {
        return uuid;
    }

    void setUuid(const sole::uuid &id) {
        TraceInput::uuid = id;
    }

    time_t getArrivalTime() const {
        return arrival_time;
    }

    void setArrivalTime(time_t arrivalTime) {
        arrival_time = arrivalTime;
    }

    std::vector<LogEntry> getLogEntries() {
        return logEntries;
    }

    void setLogEntries(const std::vector<LogEntry> &entries) {
        TraceInput::logEntries = entries;
    }

    const sole::uuid &getOutputRef() const {
        return outputRef;
    }

    void setOutputRef(const sole::uuid &oRef) {
        TraceInput::outputRef = oRef;
    }

    const std::string &getSerializedInput() const {
        return serializedInput;
    }

    void setSerializedInput(const std::string &sInput) {
        TraceInput::serializedInput = sInput;
    }

    void addTrace(const sole::uuid &id, const std::string &portName) {
        traceIdsWithPortNames.insert(std::make_pair(id, portName));
    }

    const std::multimap<sole::uuid, std::string> &getTraceIdsWithPortNames() const {
        return traceIdsWithPortNames;
    }

    template<class Archive>
    void serialize(Archive & archive)
    {
        archive( uuid, arrival_time, logEntries, outputRef, serializedInput, traceIdsWithPortNames );
    }
};
}