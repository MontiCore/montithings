/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include "lib/loguru.hpp"
#include <iostream>
#include <string>
#include <unordered_map>
#include <algorithm>
#include <vector>
#include <limits.h>

#include "../montithings-RTE/record-and-replay/recorder/utils.h"
#include "../montithings-RTE/record-and-replay/recorder/MessageWithClockContainer.h"
#include "../montithings-RTE/record-and-replay/message-types/DDSRecorderMessageTypeSupportImpl.h"
#include "../montithings-RTE/json/json.hpp"

#include "../montithings-RTE/Utils.h"

using vclock = std::unordered_map<std::string, long>;
using json = nlohmann::json;

class RecordProcessor {
private:
    static long long getFirstTimestamp(std::vector<DDSRecorderMessage::Message> messageStorage,
                                       json recordMessageDelays);

    static json
    collectMessageDelays(const std::vector<DDSRecorderMessage::Message> &messageStorage,
                         const std::string &identifier);

    static json sortRecords(json records, int minSpacing);

public:
    RecordProcessor() = default;

    ~RecordProcessor() = default;

    static json process(const std::vector<DDSRecorderMessage::Message> &messageStorage, int minSpacing);
};