/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

/**
 * Functionality dedicated to intercept and store data from multiple parts of the component implementation 
 * which is not already covered by the recording module.
 * Variables and methods are declared external so that no complex dependency is introduced.
 * 
 * This significantly simplifies managing non-deterministic calls, as this header is included in hwc blocks.
 * The nd() method can be used standalone.
 */

#pragma once

#include <iostream>
#include <iterator>
#include <unordered_map>
#include <vector>
#include <utility>

#include "../../json/json.hpp"
#include "../../Utils.h"

namespace montithings {
    namespace library {
        namespace hwcinterceptor {
            extern bool isRecording;

            // variable definitions: distinguish between recording and replaying variables in order to (theoretically) support
            // recording while replaying
            extern int indexLatencyRecording;
            extern int indexNDCallsRecording;
            extern int indexNDCallsReplaying;

            // Storages for non-deterministic calls and computation latency.
            // A map keyed by an index is used.
            // This is possible, as the replayed version behaves fully deterministic in 
            // terms of message ordering, computation quantity, etc.
            extern std::unordered_map<int, nlohmann::json> storageCalls;
            extern std::unordered_map<int, long> storageComputationLatency;

            extern void startNondeterministicRecording();

            extern void stopNondeterministicRecording();

            extern void storeCalculationLatency(long latency);

            // provides interface to fill storage at the beginning of a replay execution
            extern void addRecordedCall(int index, nlohmann::json value);

            // While recording, non-deterministic values are stored away before returning it.
            // While replaying, however, whatever is passed as a argument is discarded.
            // Instead, the recorded value is returned.
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