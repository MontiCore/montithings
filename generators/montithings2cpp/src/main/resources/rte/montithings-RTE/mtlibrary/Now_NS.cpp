/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include "MTLibrary.h"
#include <chrono>

namespace montithings {
namespace library {

std::string
now_ns() {
    auto now = std::chrono::high_resolution_clock::now();
    auto timestamp = std::chrono::duration_cast<std::chrono::nanoseconds>(now.time_since_epoch());
    return std::to_string(timestamp.count());
}

}
}