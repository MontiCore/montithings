/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "utils.h"

namespace Util
{
using namespace std::chrono;

long
Time::getCurrentTimestampUnix ()
{
  auto now_s = time_point_cast<seconds> (system_clock::now ());
  auto timestamp = now_s.time_since_epoch ();
  return timestamp.count ();
}

long long
Time::getCurrentTimestampNano ()
{
  using namespace std::chrono;
  auto now = std::chrono::high_resolution_clock::now ();
  auto timestamp = std::chrono::duration_cast<std::chrono::nanoseconds> (now.time_since_epoch ());
  return timestamp.count ();
}
} // namespace Util
