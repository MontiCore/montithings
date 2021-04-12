/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "MTLibrary.h"

namespace montithings {
namespace library {

    void
    delay(int milliseconds) {
        std::this_thread::sleep_for(std::chrono::milliseconds(milliseconds));
    }

    long
    now() {
        auto now = std::chrono::high_resolution_clock::now();
        auto timestamp = std::chrono::duration_cast<std::chrono::seconds>(now.time_since_epoch());
        return timestamp.count();
    }

    void
    log(const std::string &message) {
        LOG(INFO) << message;
    }

    std::string
    now_ns() {
        auto now = std::chrono::high_resolution_clock::now();
        auto timestamp = std::chrono::duration_cast<std::chrono::nanoseconds>(now.time_since_epoch());
        return std::to_string(timestamp.count());
    }
}
}