/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <iostream>
#include <iterator>
#include <unordered_map>
#include <vector>

#include "../../json/json.hpp"

#ifndef REPLAY_MODE
#define REPLAY_MODE "OFF"
#endif

namespace montithings {
    namespace library {
        namespace hwcinterceptor {
            extern bool isRecording;
            extern int counterLatency;
            extern int counterNd;
            extern int index;

            extern std::unordered_map<int, nlohmann::json> storageCalls;
            extern std::unordered_map<int, long> storageComputationLatency;

            extern void startNondeterministicRecording();

            extern void stopNondeterministicRecording();

            template<typename A>
            A
            nd(A value) {
                if (isRecording) {
                    storageCalls[counterNd] = nlohmann::json::object({{"v", value}});
                    counterNd++;
                }

                if (REPLAY_MODE == "ON") {
                    value = storageCalls[index].get<A>();
                    index++;
                }
                return value;
            }

            extern void storeCalculationLatency(long latency);
        }
    }
}