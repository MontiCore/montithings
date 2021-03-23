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
        static long getCurrentTimestampUnix ();
        static long long getCurrentTimestampNano ();
    };

    class Topic
    {
    public:
        static std::string getSendingInstanceNameFromTopic(const std::string& topicId) ;
        static std::string getPortNameFromTopic(const std::string& topicId);
    };
} // namespace Util
