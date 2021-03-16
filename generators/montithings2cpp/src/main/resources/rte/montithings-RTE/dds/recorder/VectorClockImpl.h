/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include "../../Utils.h"
#include <unordered_map>

using vclock = std::unordered_map<std::string, long>;

class VectorClockImpl {

private:
    vclock vectorClock;

public:
    const vclock &
    getVectorClock() const {
        return vectorClock;
    }

    std::string
    getSerializedVectorClock() {
        auto dataString = dataToJson(vectorClock);
        return dataString;
    }

    void
    updateVectorClock(const vclock &receivedVectorClock, const std::string &initiator) {
        for (auto &clock : receivedVectorClock) {
            if (vectorClock.count(clock.first) == 0 || vectorClock[clock.first] < clock.second) {
                vectorClock[clock.first] = clock.second;
            }
        }

        vectorClock[initiator]++;
    }
};
