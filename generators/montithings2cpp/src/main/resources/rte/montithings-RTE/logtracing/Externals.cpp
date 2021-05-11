/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "Externals.h"

#include "data/LogEntry.h"

namespace montithings {
    std::vector<LogTracer*> subscribers;

    void
    handleLogEntry(const std::string &message) {
        for (auto& tracer : subscribers) {
            ((LogTracer*)tracer)->handleLogEntry(message);
        }
    }

    void subscribeLogTracer(LogTracer* logTracer) {
        subscribers.push_back(logTracer);
    }
}