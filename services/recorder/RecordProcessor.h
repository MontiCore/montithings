/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include "lib/loguru.hpp"
#include <iostream>
#include <string>
#include <unordered_map>
#include <vector>

#include "../montithings-RTE/dds/recorder/utils.h"
#include "../montithings-RTE/json/json.hpp"

#include "../montithings-RTE/dds/message-types/DDSRecorderMessageTypeSupportImpl.h"

class RecordProcessor {
private:
    static long long getFirstTimestamp(std::vector<DDSRecorderMessage::Message> debugStorage,
                                       std::unordered_map<long, long> recordMessageDelays);

    static std::unordered_map<long, long>
    collectMessageDelays(const std::vector<DDSRecorderMessage::Message> &debugStorage,
                         const std::string &identifier);

public:
    RecordProcessor() = default;

    ~RecordProcessor() = default;

    static nlohmann::json process(const std::vector<DDSRecorderMessage::Message> &debugStorage);
};