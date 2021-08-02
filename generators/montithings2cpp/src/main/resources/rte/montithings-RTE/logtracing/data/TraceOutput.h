/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include "sole/sole.hpp"
#include "LogEntry.h"
#include "TraceInput.h"

namespace montithings {
    class TraceOutput {
    private:
        sole::uuid uuid{};

        std::vector<TraceInput> inputs;

        // outputs can contain trade IDs as well. This can be, for instance, forwarding ports due to decomposition
        // e.g. hierarchy.Example.d.sum.result -> hierarchy.Example.d.y, whereas port y is a outgoing port
        std::multimap<sole::uuid, std::string> traceIdsWithPortNames;

    public:
        TraceOutput() {
            uuid = sole::uuid4();
        }

        virtual ~TraceOutput() = default;

        const sole::uuid &getUuid() const {
            return uuid;
        }

        void setUuid(const sole::uuid &id) {
            TraceOutput::uuid = id;
        }

        const std::vector<TraceInput> &getInputs() const {
            return inputs;
        }

        void setInputs(const std::vector<TraceInput> &in) {
            TraceOutput::inputs = in;
        }

        void addTrace(const sole::uuid &id, const std::string &portName) {
            traceIdsWithPortNames.insert(std::make_pair(id, portName));
        }

        const std::multimap<sole::uuid, std::string> &getTraceIdsWithPortNames() const {
            return traceIdsWithPortNames;
        }

        template<class Archive>
        void serialize(Archive & archive)
        {
            archive( uuid, inputs, traceIdsWithPortNames );
        }
    };
}