/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "HWCInterceptor.h"

namespace montithings {
    namespace library {
        namespace hwcinterceptor {
            bool isRecording = false;
            int counterLatency = 0;
            int counterNd = 0;
            int index = 0;

            std::unordered_map<int, nlohmann::json> storageCalls;
            std::unordered_map<int, long> storageComputationLatency;

            void
            startNondeterministicRecording() {
                isRecording = true;
                counterLatency = 0;
                counterNd = 0;
                index = 0;

                storageCalls.clear();
                storageComputationLatency.clear();
            }

            void
            stopNondeterministicRecording() {
                isRecording = false;
            }

            void
            storeCalculationLatency(long latency) {
                if (isRecording) {
                    storageComputationLatency[counterLatency] = latency;
                    counterLatency++;
                }
            }
        }
    }
}