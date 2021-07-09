/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "utils.h"

namespace Util {
    using namespace std::chrono;

    long
    Time::getCurrentTimestampUnix() {
        auto now_s = time_point_cast<seconds>(system_clock::now());
        auto timestamp = now_s.time_since_epoch();
        return timestamp.count();
    }

    long long
    Time::getCurrentTimestampNano() {
        using namespace std::chrono;
        auto now = std::chrono::high_resolution_clock::now();
        auto timestamp = std::chrono::duration_cast<std::chrono::nanoseconds>(now.time_since_epoch());
        return timestamp.count();
    }

    std::string
    Topic::getSendingInstanceNameFromTopic(const std::string& topicId) {
        return topicId.substr(0, topicId.find_last_of('.'));
    }

    // adapted from https://www.techiedelight.com/split-string-cpp-using-delimiter/
    std::string
    Topic::getPortNameFromTopic(const std::string& topicId) {
        if (topicId.size() == 0) {
            return topicId;
        }

        std::vector<std::string> out;

        std::string::size_type beg = 0;
        for (auto end = 0; (end = topicId.find('.', end)) != std::string::npos; ++end) {
            out.push_back(topicId.substr(beg, end - beg));
            beg = end + 1;
        }

        out.push_back(topicId.substr(beg));

        std::string postfix = out.at(out.size() - 1);

        return postfix.substr(0, topicId.find_last_of('/'));
    }
} // namespace Util
