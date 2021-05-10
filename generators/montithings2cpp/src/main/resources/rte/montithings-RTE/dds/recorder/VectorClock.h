/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <unordered_map>
#include <mutex>
#include <vector>

#include "../../Utils.h"

using vclock = std::unordered_map<std::string, long>;

namespace VectorClock
{
    extern std::mutex updateClockMutex;

    extern vclock vectorClock;

    extern const vclock & getVectorClock ();

    extern std::string getSerializedVectorClock ();

    extern void updateVectorClock(const vclock &receivedVectorClock, const std::string &initiator);
}
