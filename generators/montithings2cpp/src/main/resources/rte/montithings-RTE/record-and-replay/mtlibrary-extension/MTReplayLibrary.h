/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

/**
 * Library Extension which are included in hwc when replaying is enabled.
 * Methods defined here are used by model transformations.
 * This way, behavior can be defined in behavior{} blocks instead of going the more complex way of generating hwc files. 
 */

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
            // used to store indexed timestamps or delay information in nanoseconds
            extern std::unordered_map<int, unsigned long long> nsMap;

            void storeNsInMap(int index, unsigned long long ts);

            unsigned long long getNsFromMap(int index);

            // very simple substraction of two numerical value returning an integer >= 0
            // simplified generation of behavior
            template<typename A, typename B>
            A subtract(A v1, B v2) {
                if (v1 - v2 < 0) {
                    return 0;
                }
                return v1 - v2;
            }

            // support for delays with a high resolution
            void delayNanoseconds(unsigned long nanoseconds);

            unsigned long long getNanoTimestamp();
        }
    }
}