/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "string"
#include <map>
#include <vector>
#include <utility>
#include <ctime>

#include "sole/sole.hpp"

#include "Collector.h"

namespace montithings {
namespace logtracer {
namespace utils {
    template<typename T>
    std::pair<sole::uuid, T> piggypackId(T value){
        return std::make_pair(montithings::logtracer::collector::currOutputLogsId, value);
    }
}
}
}