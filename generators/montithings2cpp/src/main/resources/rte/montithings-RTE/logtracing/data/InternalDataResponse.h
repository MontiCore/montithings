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
        InternalDataResponse(const std::map<std::string, std::string> &varSnapshot, const std::string &input,
                             const std::vector<std::string> &traceUuids) : varSnapshot(varSnapshot), input(input),
                                                                           traceUuids(traceUuids) {}

        InternalDataResponse() = default;

        virtual ~InternalDataResponse() =default;

        const std::map<std::string, std::string> &getVarSnapshot() const;

        void setVarSnapshot(const std::map<std::string, std::string> &varSnapshot);

        const std::string &getInput() const;

        void setInput(const std::string &input);

        const std::vector<std::string> &getTraceUuids() const ;

        void setTraceUuids(const std::vector<std::string> &traceUuids);

        template<class Archive>
        void serialize(Archive & archive)
        {
            archive( time, varSnapshot, varSnapshot, varSnapshot );
        }
    };
}