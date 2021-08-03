/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "HWCInterceptor.h"

namespace montithings {
    namespace library {
        namespace hwcinterceptor {
            bool isRecording = false;
            int indexLatencyRecording = 0;
            int indexNDCallsRecording = 0;
            int indexNDCallsReplaying = 0;

            std::unordered_map<int, nlohmann::json> storageCalls;
            std::unordered_map<int, long> storageComputationLatency;

            void
            startNondeterministicRecording() {
                isRecording = true;
                indexLatencyRecording = 0;
                indexNDCallsRecording = 0;

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
                    storageComputationLatency[indexLatencyRecording] = latency;
                    indexLatencyRecording++;
                }
            }

            void
            addRecordedCall(int index, nlohmann::json value) {
                storageCalls[index] = std::move(value);
            }
        }
    }
}