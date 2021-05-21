/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "InternalDataResponse.h"

namespace montithings {
    const std::map<std::string, std::string> &InternalDataResponse::getVarSnapshot() const {
        return varSnapshot;
    }

    void InternalDataResponse::setVarSnapshot(const std::map<std::string, std::string> &varSnapshot) {
        InternalDataResponse::varSnapshot = varSnapshot;
    }

    const std::string &InternalDataResponse::getInput() const {
        return input;
    }

    void InternalDataResponse::setInput(const std::string &input) {
        InternalDataResponse::input = input;
    }

    const std::multimap<std::string, std::string> &InternalDataResponse::getTracesUuidsWithPortNames() const {
        return tracesUuidsWithPortNames;
    }

    void InternalDataResponse::setTracesUuidsWithPortNames(const std::multimap<std::string, std::string> &traceUuids) {
        InternalDataResponse::tracesUuidsWithPortNames = traceUuids;
    }

    const std::map<std::string, std::string> &InternalDataResponse::getSourcesOfPortsMap() const {
        return sourcesOfPortsMap;
    }

    void InternalDataResponse::setSourcesOfPortsMap(const std::map<std::string, std::string> &sourcesOfPortsMap) {
        InternalDataResponse::sourcesOfPortsMap = sourcesOfPortsMap;
    }
}