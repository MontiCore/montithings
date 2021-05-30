/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include <string>
#include <map>
#include <vector>
#include <utility>
#include <ctime>

#include "sole/sole.hpp"

#include "LogTracer.h"
#include "data/LogEntry.h"

namespace montithings {
    // forward declaring
    class LogTracer;

    extern std::vector<LogTracer*> subscribers;

    void subscribeLogTracer(LogTracer* logTracer);
    void handleLogEntry(const std::string &message);

}