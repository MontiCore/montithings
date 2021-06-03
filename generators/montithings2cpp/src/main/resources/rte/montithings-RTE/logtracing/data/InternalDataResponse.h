/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "sole/sole.hpp"
#include <string>
#include <map>
#include <utility>

namespace montithings {

    class InternalDataResponse {
    private:
        std::map<std::string, std::string> sourcesOfPortsMap;
        std::map<std::string, std::string> varSnapshot;
        std::string input;
        std::multimap<sole::uuid, std::string> tracesUuidsWithPortNames;

    public:
        InternalDataResponse(std::map<std::string, std::string> sourcesOfPortsMap,
                             std::map<std::string, std::string> varSnapshot,
                             std::string input,
                             std::multimap<sole::uuid, std::string> tracesUuidsWithPortNames)
                             :  sourcesOfPortsMap(std::move(sourcesOfPortsMap)),
                                varSnapshot(std::move(varSnapshot)),
                                input(std::move(input)),
                                tracesUuidsWithPortNames(std::move(tracesUuidsWithPortNames)) {}

        InternalDataResponse() = default;

        virtual ~InternalDataResponse() =default;

        const std::map<std::string, std::string> &getSourcesOfPortsMap() const;

        void setSourcesOfPortsMap(const std::map<std::string, std::string> &sourcesOfPortsMap);

        const std::map<std::string, std::string> &getVarSnapshot() const;

        void setVarSnapshot(const std::map<std::string, std::string> &varSnapshot);

        const std::string &getInput() const;

        void setInput(const std::string &input);

        const std::multimap<sole::uuid, std::string> &getTracesUuidsWithPortNames() const ;

        void setTracesUuidsWithPortNames(const std::multimap<sole::uuid, std::string> &tracesUuidsWithPortNames);

        template<class Archive>
        void serialize(Archive & archive)
        {
            archive( sourcesOfPortsMap, varSnapshot, input, tracesUuidsWithPortNames );
        }
    };
}