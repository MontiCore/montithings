/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include <thread>

#include "easyloggingpp/easylogging++.h"

#include "../../mtlibrary/MTLibrary.h"
#include "../Collector.h"

namespace montithings
{
namespace library
{

void
log (const std::string& message)
{
  LOG(INFO) << message;
  montithings::logtracer::collector::handleLogEntry(message);
}

}
}