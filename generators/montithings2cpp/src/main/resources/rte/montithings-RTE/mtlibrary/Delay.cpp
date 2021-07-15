/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include "MTLibrary.h"
#include <thread>


namespace montithings {
namespace library {

void
delay (int milliseconds)
{
  std::this_thread::sleep_for (std::chrono::milliseconds (milliseconds));
}

}
}