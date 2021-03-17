/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "VectorClock.h"

namespace VectorClock
{
    std::mutex updateClockMutex;
    vclock vectorClock;

    const vclock &
    getVectorClock() {
        return vectorClock;
    }

    std::string
    getSerializedVectorClock() {
        return dataToJson(vectorClock);
    }

    void
    updateVectorClock(const vclock &receivedVectorClock, const std::string &initiator) {
        std::cout << "trying to update clock for  " << initiator << std::endl;
        // ensure there are no parallel updates
        std::lock_guard<std::mutex> guard(updateClockMutex);

        std::cout << "after mutex for  " << initiator << std::endl;
        for (auto &clock : receivedVectorClock) {
            if (vectorClock.count(clock.first) == 0 || vectorClock[clock.first] < clock.second) {
                vectorClock[clock.first] = clock.second;
            }
        }

        vectorClock[initiator]++;
        std::cout << "finish for  " << initiator << std::endl;
    }
}
