/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

/**
 * At some locations the log tracer instance cannot be accessed.
 * -> The intercepted log method is very low level and does not know the LogTracer instance
 * -> model behavior implemented in plain C++ not using the provided log method cannot tell when log entries are happening
 * 
 * In order to support both, an external subscription list of LogTracers are maintained which are notified upon logging calls.
 * Since MontiThings allow to package multiple components into one binary, multiple LogTracers can be maintained here.
 * 
 * Note that multiple LogTracers lead to the known issue that log entries cannot be assigned to the correct LogTracer.     
 */

#pragma once
#include <string>
#include <map>
#include <vector>
#include <utility>
#include <ctime>

#include "sole/sole.hpp"

#include "LogTracer.h"

namespace montithings {
    // forward declaring
    class LogTracer;

    namespace logtracing {

        extern std::vector<LogTracer *> attachedTracers;

        void attachLogTracer(LogTracer *logTracer);

        void handleLogEntry(const std::string &message);
    }
}