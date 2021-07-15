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
    // Avoid simultaneous clock updates.
    extern std::mutex updateClockMutex;

    extern vclock vectorClock;

    extern const vclock & getVectorClock ();

    // Support to return the vector clock as a string.
    // This is used while piggybacking the clock to exchanged messages 
    extern std::string getSerializedVectorClock ();

    // updating requires the received clock and the instance name of the sender
    extern void updateVectorClock(const vclock &receivedVectorClock, const std::string &initiator);
}
