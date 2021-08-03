/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <chrono>
#include <string>
#include <vector>
#include <algorithm>

namespace Util
{
    class Time
    {
    public:
        // returns unix timestamp (seconds)
        static long getCurrentTimestampUnix ();

        // returns timestamp in nanoseconds
        static long long getCurrentTimestampNano ();
    };

    class Topic
    {
    public:
        // helper methods to parse information from topic strings
        static std::string getSendingInstanceNameFromTopic(const std::string& topicId) ;
        static std::string getPortNameFromTopic(const std::string& topicId);
    };
} // namespace Util
