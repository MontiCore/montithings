/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <chrono>

namespace Util
{
class Time
{
public:
  static long getCurrentTimestampUnix ();
  static long long getCurrentTimestampNano ();
};
} // namespace Util
