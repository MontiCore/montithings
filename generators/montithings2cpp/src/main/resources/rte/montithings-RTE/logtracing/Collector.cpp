/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "Collector.h"

namespace montithings {
namespace logtracer {
namespace collector {
    std::map<sole::uuid, std::pair<time_t, std::string>> logEntries;

    std::vector<sole::uuid> currInputLogs;
    sole::uuid currInputLogsId;
    std::map<sole::uuid, std::vector<sole::uuid>> allInputLogs;

    std::vector<sole::uuid> currOutputLogs;
    sole::uuid currOutputLogsId;
    std::map<sole::uuid, std::vector<sole::uuid>> allOutputLogs;

    sole::uuid
    uuid(){
        return sole::uuid4 ();
    }

    void
    handleLogEntry(const std::string& message) {
        sole::uuid id = uuid();
        logEntries[id] = std::make_pair(time(nullptr), message);
        currInputLogs.push_back(id);
    }

    void
    handleInput() {
        sole::uuid id = uuid();
        allInputLogs[id] = currInputLogs;
        currInputLogs.clear();
    }

    void
    handleOutput() {
        sole::uuid id = uuid();
        allOutputLogs[id] = currOutputLogs;
        currOutputLogs.clear();
    }
}
}
}