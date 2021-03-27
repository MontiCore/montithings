/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <iostream>
#include <iterator>
#include <unordered_map>
#include <vector>

#include "../../json/json.hpp"

#define RECORDER_MODE "RECORDING"

namespace HWCInterceptor
{
extern bool isRecording;
extern int counterLatency;
extern int counterNd;
extern int index;

extern nlohmann::json storage;
extern std::unordered_map<int, nlohmann::json> storageCalls;
extern std::unordered_map<int, long> storageComputationLatency;

extern void startNondeterministicRecording ();

extern void stopNondeterministicRecording ();

template <typename A>
A
nd (A value)
{
  if (RECORDER_MODE == "RECORDING" && isRecording)
    {
      storageCalls[counterNd] = nlohmann::json::object ({ { "v", value } });
      storage["calls"][counterNd] = value;
      counterNd++;
    }

  if (RECORDER_MODE == "REPLAYING")
    {
      value = storageCalls[index].get<A> ();
      value = storage[index].get<A> ();
      index++;
    }
  return value;
}

extern void storeCalculationLatency (long latency);
} // namespace Recorder