/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "MTReplayLibrary.h"

namespace montithings {
    namespace library {
        namespace replayer {
            std::unordered_map<int, unsigned long long> messageTsMap;

            void storeMsgTs(int index, unsigned long long ts) {
                messageTsMap[index] = ts;
            }

            unsigned long long getMsgTs(int index) {
                if (messageTsMap.find(index) == messageTsMap.end()) {
                    // no such key, return 0
                    return 0;
                }
                return messageTsMap[index];
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