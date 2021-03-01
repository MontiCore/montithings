/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

//#include "../../cereal/archives/json.hpp"
//#include "../../Utils.h"
#include <unordered_map>

//#include "../../cereal/types/unordered_map.hpp"

class VectorClockImpl
{
  using clock = std::unordered_map<std::string, long>;

private:
  clock vectorClock;

public:
  const clock &
  getVectorClock () const
  {
    return vectorClock;
  }

  static const char *
  getSerializedVectorClock ()
  {
    auto dataString = "test"; // dataToJson (vectorClock);
    return dataString;        //.c_str ();
  }

  void
  updateVectorClock (const clock &receivedVectorClock, const std::string& initiator)
  {
    for (auto &clock : receivedVectorClock)
      {
        if (vectorClock.count (clock.first) == 0 || vectorClock[clock.first] < clock.second)
          {
            vectorClock[clock.first] = clock.second;
          }
      }

    vectorClock[initiator]++;
  }
};
