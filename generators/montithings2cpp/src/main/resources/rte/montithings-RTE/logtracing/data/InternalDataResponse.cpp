/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "sole/sole.hpp"
#include <string>
#include <map>

namespace montithings {

class InternalDataResponse {
private:
    std::map<std::string, std::string> varSnapshot;
    std::string input;
    std::vector<std::string> traceUuids;

public:

    template<class Archive>
    void serialize(Archive & archive)
    {
        archive( time, content, inputUuid, outputUuid );
    }
};
}