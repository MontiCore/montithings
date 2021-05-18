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

    const std::vector<std::string> &InternalDataResponse::getTraceUuids() const {
        return traceUuids;
    }

    void InternalDataResponse::setTraceUuids(const std::vector<std::string> &traceUuids) {
        InternalDataResponse::traceUuids = traceUuids;
    }
}