/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "string"
#include <map>
#include <vector>
#include <utility>
#include <ctime>
#include <map>

#include "sole/sole.hpp"
#include "Utils.h"

#include "data/LogEntry.h"
#include "GlobalStorage.h"

namespace montithings {

class LogTracer {
private:
    std::string instanceName;

    // stores all log messages with their timestamp, referenced by a sole::uuid
    std::map<sole::uuid, LogEntry> logEntries;

    // there can be multiple log entries for the same input messages, thus, they are grouped
    // currInputLogs collects all input log UUIDs until a new input arrives
    std::vector<sole::uuid> currInputLogs;
    sole::uuid currInputLogsId{};
    // the currInputLogs vector is then referenced by a new UUID, stored in inputLogs, and cleared
    std::map<sole::uuid, std::vector<sole::uuid>> allInputLogs;

    // store a JSON serialized snapshot of the input
    std::map<sole::uuid, std::string> serializedInputs;

    // there can be multiple InputLogs for the same output message, thus they are grouped as well; analogously to the inputLogs
    std::vector<sole::uuid> currOutputLogs;
    sole::uuid currOutputLogsId{};
    std::map<sole::uuid, std::vector<sole::uuid>> allOutputLogs;

    // in order to store variable states, a map (key=variable name) holds a map where the variable value is keyed by the timestamp which indicates when it was changed
    // the variable value is stored as a string to avoid type clashes
    using vSnapshot = std::map<time_t, std::string>;
    std::map<std::string, vSnapshot> variableSnapshots;

    // creates new UUID
    sole::uuid uuid();

public:
    explicit LogTracer(std::string instanceName);

    ~LogTracer() = default;

    sole::uuid newOutput();

    void handleLogEntry(const std::string& content);

    template <typename T>
    void handleVariableStateChange(const std::string& variableName, const T& valueBefore, const T& valueAfter) {
        if (valueBefore != valueAfter) {
            variableSnapshots[variableName][time(nullptr)] = std::to_string(valueAfter);
        }
    }

    template <typename T>
    void handleInput(const T& input) {
        // stores the log entries which are grouped to the previous input
        allInputLogs[currInputLogsId] = currInputLogs;

        // a new input arrived, hence clear the current group
        currInputLogs.clear();

        // updates current id with a fresh one
        currInputLogsId = uuid();

        // store serialized input
        serializedInputs[currInputLogsId] = dataToJson(input);
    }
};


}