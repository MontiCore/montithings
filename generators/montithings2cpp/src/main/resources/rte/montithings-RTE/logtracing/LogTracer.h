/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

/**
 * The log tracer module instantiated by component instances.
 */

#pragma once

#include <string>
#include <map>
#include <vector>
#include <utility>
#include <algorithm>
#include <ctime>
#include <map>

#include "sole/sole.hpp"
#include "Utils.h"

#include "interface/LogTracerInterface.h"
#include "data/LogEntry.h"
#include "data/TraceOutput.h"
#include "data/InternalDataResponse.h"
#include "Externals.h"

namespace montithings {
    class LogTracer {
    private:
        // interface for communication with the middleware and components
        LogTracerInterface *interface;

        std::string instanceName;

        long logEntryIndex;

        // there can be multiple log entries for the same input messages, thus, they are grouped
        // currInputLogs collects all input log UUIDs until a new input arrives
        std::vector<LogEntry> currInputLogs;

        // having the inputs is fine, but we although want to know which instance has sent the corresponding input
        // therefore keep track of the source instance names
        std::map<std::string, std::string> sourcesOfPortsMap;

        TraceInput currTraceInput;
        TraceOutput currTraceOutput;

        // there can be multiple InputLogs for the same output message, thus they are grouped as well; analogously to the inputLogs
        std::vector<TraceInput> currInputGroup;
        std::vector<TraceOutput> traceOutputs;

        //input UUIDs belong to a certain port, keep track of it
        std::multimap<sole::uuid, std::string> inputTracePortNameRefs;

        // store which ports are external. This is done to visualize external ports differently
        std::vector<std::string> externalPorts;

        // in order to store variable states, a map (key=variable name) holds a map where the variable value is keyed by the timestamp which indicates when it was changed
        // the variable value is stored as a string to avoid type clashes
        using vSnapshot = std::map<time_t, std::string>;
        std::map<std::string, vSnapshot> variableSnapshots;

        std::vector<LogEntry> getAllLogEntries();

        tl::optional<LogEntry> getLogEntryByUuid(sole::uuid uuid);

        tl::optional<TraceInput> getInputByUuid(sole::uuid uuid);

        tl::optional<TraceOutput> getOutputByUuid(sole::uuid uuid);

        tl::optional<TraceOutput> getOutputByInputUuid(sole::uuid uuid);

        // creates new UUIDs
        static sole::uuid uuid();

        // helper function to turn any primitive type supported by MontiCore to a string
        static std::string toString(std::string value) {
            // its already a string, nothing to do
            return value;
        }

        template<typename T>
        static std::string toString(T value) {
            return std::to_string(value);
        }

    public:
        explicit LogTracer(std::string instanceName, LogTracerInterface &interface);

        ~LogTracer() = default;

        LogTracerInterface *getInterface();

        sole::uuid getCurrOutputUuid();

        TraceInput &getCurrTraceInput();

        TraceOutput &getCurrTraceOutput();

        std::vector<TraceInput> &getCurrInputGroup();

        void handleOutput();

        void resetCurrentOutput();

        void resetCurrentInput();

        void handleLogEntry(const std::string &content);

        void onRequest(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid,
                       LogTracerInterface::Request reqType, long fromTimestamp);

        void sendLogEntries(sole::uuid reqUuid, long fromTimestamp);

        void sendTraceData(sole::uuid reqUuid, sole::uuid traceUuid);

        void sendInternalData(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid);

        void mapPortToSourceInstance(std::string portName, std::string instanceName);

        void registerExternalPort(std::string portName);

        std::map<std::string, std::string> getVariableSnapshot(time_t time);

        std::multimap<sole::uuid, std::string> getTraceUuids(sole::uuid inputUuid);

        std::multimap<sole::uuid, std::string> getTraceUuidsDecomposed(sole::uuid outputUuid);

        template<typename T>
        void handleVariableStateChange(const std::string &variableName, const T &valueBefore, const T &valueAfter) {
            if (valueBefore != valueAfter) {
                variableSnapshots[variableName][time(nullptr)] = toString(valueAfter);
            }
        }

        template<typename T>
        void handleInput(const T &input, const std::multimap<sole::uuid, std::string> &traceUUIDs) {
            // stores the log entries which are grouped to the previous input
            currTraceInput.setLogEntries(currInputLogs);

            // a new input arrived, hence clear the current log entry group
            currInputLogs.clear();

            // store serialized input
            currTraceInput.setSerializedInput(dataToJson(input));

            currTraceInput.setArrivalTime(time(nullptr));

            // Add reference to uuids sent along with the input
            for (auto const &trace : traceUUIDs) {
                // trace.first == uuid
                // trace.second == port name
                currTraceInput.addTrace(trace.first, trace.second);
            }

            // Add input to the current group
            currInputGroup.push_back(currTraceInput);

            currTraceInput = TraceInput();
        }
    };
}