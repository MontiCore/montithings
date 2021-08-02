/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include "Externals.h"

namespace montithings {
    namespace logtracing {
        std::vector<LogTracer *> attachedTracers;

        void
        handleLogEntry(const std::string &message) {
            for (auto &tracer : attachedTracers) {
                ((LogTracer *) tracer)->handleLogEntry(message);
            }
        }

        void attachLogTracer(LogTracer *logTracer) {
            attachedTracers.push_back(logTracer);
        }
    }
}