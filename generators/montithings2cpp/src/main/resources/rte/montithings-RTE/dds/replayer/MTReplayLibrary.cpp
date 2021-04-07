/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "MTReplayLibrary.h"

namespace montithings {
    namespace library {
        namespace replayer {
            std::unordered_map<int, unsigned long long> nsMap;

            void storeNsInMap(int index, unsigned long long ts) {
                nsMap[index] = ts;
            }

            unsigned long long getNsFromMap(int index) {
                if (nsMap.find(index) == nsMap.end()) {
                    // no such key, return 0
                    return 0;
                }
                return nsMap[index];
            }

            void
            delayNanoseconds(unsigned long nanoseconds) {
                std::this_thread::sleep_for(std::chrono::nanoseconds(nanoseconds));
            }

            unsigned long long
            getNanoTimestamp() {
                auto now = std::chrono::high_resolution_clock::now();
                auto timestamp = std::chrono::duration_cast<std::chrono::microseconds>(now.time_since_epoch());
                return timestamp.count();
            }

        }
    }
}