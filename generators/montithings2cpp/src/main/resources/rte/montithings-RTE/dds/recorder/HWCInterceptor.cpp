/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "HWCInterceptor.h"

namespace HWCInterceptor {
    bool isRecording = false;
    int counterLatency = 0;
    int counterNd = 0;
    int index = 0;
    nlohmann::json storage;

    std::unordered_map<int, nlohmann::json> storageCalls;
    std::unordered_map<int, long> storageComputationLatency;

    void
    startNondeterministicRecording() {
        isRecording = true;
        counterLatency = 0;
        counterNd = 0;
        index = 0;

        storage = nlohmann::json::object();
        storageCalls.clear();
        storageComputationLatency.clear();
    }

    void
    stopNondeterministicRecording() {
        isRecording = false;
    }

    void
    storeCalculationLatency(long latency) {
        if (RECORDER_MODE == "RECORDING" && isRecording) {
            storageComputationLatency[counterLatency] = latency;
            storage["calc_latency"][counterLatency] = latency;
            counterLatency++;
        }
    }
} // namespace Recorder