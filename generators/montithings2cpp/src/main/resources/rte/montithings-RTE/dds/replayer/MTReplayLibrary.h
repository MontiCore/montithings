/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include <string>
#include <chrono>
#include <thread>
#include <unordered_map>
#include <cstdlib>

#include "cereal/archives/json.hpp"

#include "Utils.h"


namespace montithings {
    namespace library {
        namespace replayer {
            extern std::unordered_map<int, unsigned long long> messageTsMap;

            void storeMsgTs(int index, unsigned long long ts);

            unsigned long long getMsgTs(int index);

            template<typename T>
            T diff(T v1, T v2) {
                return abs(v1 - v2);
            }

            void
            delayNanoseconds(unsigned long nanoseconds);

            unsigned long long
            getNanoTimestamp();
        }
    }
}