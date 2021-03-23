/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "MTLibrary.h"
#include <thread>
#include "easyloggingpp/easylogging++.h"

namespace montithings
{
namespace library
{

void
delay (int milliseconds)
{
  std::this_thread::sleep_for (std::chrono::milliseconds (milliseconds));
}

void
log (const std::string& message)
{
  LOG(INFO) << message;
}

}
}