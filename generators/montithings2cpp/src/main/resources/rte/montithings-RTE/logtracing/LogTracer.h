/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "string"
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
#include "data/InternalDataResponse.h"
#include "Externals.h"


namespace montithings {

class LogTracer {
private:
    LogTracerInterface* interface;

    std::string instanceName;

    // stores all log messages with their timestamp, referenced by a sole::uuid
    std::map<sole::uuid, LogEntry> logEntries;

    // there can be multiple log entries for the same input messages, thus, they are grouped
    // currInputLogs collects all input log UUIDs until a new input arrives
    std::vector<sole::uuid> currInputLogs;
    // the currInputLogs vector is then referenced by a new UUID, stored in inputLogs, and cleared
    std::map<sole::uuid, std::vector<sole::uuid>> allInputLogs;
    // when a new input arrives, a new input ID is created
    sole::uuid currInputId{};
    //output UUIDs from messages are referenced with the current InputId
    std::multimap<sole::uuid, sole::uuid> outputInputRefs;



    // store a JSON serialized snapshot of the input
    std::map<sole::uuid, std::string> serializedInputs;

    // having the inputs is fine, but we although want to know which instance has sent the corresponding input
    // therefore keep track of the source instance names
    std::map<std::string, std::string> sourcesOfPortsMap;

    // there can be multiple InputLogs for the same output message, thus they are grouped as well; analogously to the inputLogs
    std::vector<sole::uuid> currOutputLogs;
    sole::uuid currOutputId{};
    std::map<sole::uuid, std::vector<sole::uuid>> allOutputLogs;

    //input UUIDs belong to a certain port, keep track of it
    std::multimap<sole::uuid, std::string> inputTracePortNameRefs;

    // in order to store variable states, a map (key=variable name) holds a map where the variable value is keyed by the timestamp which indicates when it was changed
    // the variable value is stored as a string to avoid type clashes
    using vSnapshot = std::map<time_t, std::string>;
    std::map<std::string, vSnapshot> variableSnapshots;

    // creates new UUID
    static sole::uuid uuid();

public:
    explicit LogTracer(std::string instanceName, LogTracerInterface &interface);

    ~LogTracer() = default;

    LogTracerInterface * getInterface();

    sole::uuid newOutput();

    void resetCurrentOutput();

    void resetCurrentInput();

    void handleLogEntry(const std::string& content);

    void onRequest(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid, LogTracerInterface::Request reqType, long fromTimestamp);

    void sendLogEntries(sole::uuid reqUuid, long fromTimestamp);

    void sendInternalData(sole::uuid reqUuid, sole::uuid logUuid, sole::uuid inputUuid, sole::uuid outputUuid);

    void mapPortToSourceInstance(std::string portName, std::string instanceName);

    std::map<std::string, std::string> getVariableSnapshot(time_t time);

    std::multimap<std::string, std::string> getTraceUuids(sole::uuid inputUuid);

    template <typename T>
    void handleVariableStateChange(const std::string& variableName, const T& valueBefore, const T& valueAfter) {
        if (valueBefore != valueAfter) {
            variableSnapshots[variableName][time(nullptr)] = std::to_string(valueAfter);
        }
    }

    template <typename T>
    void handleInput(const T& input, const std::multimap<sole::uuid, std::string>& traceUUIDs) {
        // stores the log entries which are grouped to the previous input
        allInputLogs[currInputId] = currInputLogs;

        // a new input arrived, hence clear the current group
        currInputLogs.clear();

        // updates current id with a fresh one
        currInputId = uuid();

        // store serialized input
        serializedInputs[currInputId] = dataToJson(input);

        // Add reference to uuids sent along with the input
        for (auto const &trace : traceUUIDs) {
            // register, that the trace is linked to the current input
            outputInputRefs.insert(std::make_pair(currInputId, trace.first));

            // register, that the uuid is associated with a certain port
            inputTracePortNameRefs.insert(std::make_pair(trace.first, trace.second));
        }
    }
};


}