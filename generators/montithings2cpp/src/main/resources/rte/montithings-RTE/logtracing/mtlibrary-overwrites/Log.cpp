/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

/**
 * Overwriting implementation of the log method which notifies corresponding LogTracers.
 * This file is swapped within CMAKE in case of log tracing is enabled.
 */

#include <thread>

#include "easyloggingpp/easylogging++.h"

#include "../../mtlibrary/MTLibrary.h"
#include "../LogTracer.h"

namespace montithings
{
namespace library
{

void
log (const std::string& message)
{
    LOG(INFO) << message;
    montithings::logtracing::handleLogEntry(message);
}

}
}