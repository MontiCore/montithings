/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <iostream>
#include <iterator>
#include <unordered_map>
#include <vector>

#include "../../json/json.hpp"
#include "../../Utils.h"


namespace montithings {
    namespace library {
        namespace hwcinterceptor {
            extern bool isRecording;

            // variable definitions: distinguish between recording and replaying variables in order to support
            // recording while replaying
            extern int indexLatencyRecording;
            extern int indexNDCallsRecording;
            extern int indexNDCallsReplaying;

            extern std::unordered_map<int, nlohmann::json> storageCalls;
            extern std::unordered_map<int, long> storageComputationLatency;

            extern void startNondeterministicRecording();

            extern void stopNondeterministicRecording();

            extern void storeCalculationLatency(long latency);

            extern void addRecordedCall(int index, nlohmann::json value);

            template<typename A>
            A
            nd(A value) {
                if (isRecording) {
                    storageCalls[indexNDCallsRecording] = dataToJson(value);
                    indexNDCallsRecording++;
                }

#if defined(REPLAY_MODE)
                value = jsonToData<A>(storageCalls[indexNDCallsReplaying]);
                indexNDCallsReplaying++;
#endif

                return value;
            }
        }
    }
}