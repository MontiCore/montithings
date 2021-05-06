/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "string"
#include <map>
#include <vector>
#include <utility>
#include <ctime>

#include "sole/sole.hpp"

namespace montithings
{
namespace logtracer
{
namespace collector
{
    // stores all log messages with their timestamp, referenced by a sole::uuid
    extern std::map<sole::uuid, std::pair<time_t, std::string>> logEntries;

    // there can be multiple log entries for the same input messages, thus, they are grouped
    // currInputLogs collects all input log UUIDs until a new input arrives
    extern std::vector<sole::uuid> currInputLogs;
    extern sole::uuid currInputLogsId;
    // the currInputLogs vector is then referenced by a new UUID, stored in inputLogs, and cleared
    extern std::map<sole::uuid, std::vector<sole::uuid>> allInputLogs;

    // there can be multiple InputLogs for the same output message, thus they are grouped as well; analogously to the inputLogs
    extern std::vector<sole::uuid> currOutputLogs;
    extern sole::uuid currOutputLogsId;
    extern std::map<sole::uuid, std::vector<sole::uuid>> allOutputLogs;

    // creates new UUID
    sole::uuid uuid();

    void handleInput();

    void handleOutput();

    void handleLogEntry(const std::string& message);
}
}
}