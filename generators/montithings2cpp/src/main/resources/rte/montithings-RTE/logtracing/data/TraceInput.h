/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include <map>
#include "sole/sole.hpp"
#include "LogEntry.h"

namespace montithings {
    class TraceInput {
    private:
        sole::uuid uuid{};
        time_t arrivalTime;
        std::vector<LogEntry> logEntries;
        sole::uuid outputRef{};
        std::string serializedInput;

        // Traces always belong to a certain port.
        // Note that a trace can be linked to multiple incoming ports (e.g. Sum within the Doubler component)
        std::multimap<sole::uuid, std::string> traceIdsWithPortNames;

    public:
        TraceInput()  {
            arrivalTime = time(nullptr);
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
            return arrivalTime;
        }

        void setArrivalTime(time_t aTime) {
            arrivalTime = aTime;
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
            archive( uuid, arrivalTime, logEntries, outputRef, serializedInput, traceIdsWithPortNames );
        }
    };
}