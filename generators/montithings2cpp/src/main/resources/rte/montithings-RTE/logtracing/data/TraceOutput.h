/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "sole/sole.hpp"
#include "LogEntry.h"
#include "TraceInput.h"

namespace montithings {
    class TraceOutput {
    private:
        sole::uuid uuid{};

        std::vector<TraceInput> inputs;

        // outgoing ports can forward outputs from subcomponents
        // therefore we need to reference these output uuids in order to allow tracing
        // otherwise we lose track, as the main component will generate a new output with a fresh uuid which is not related to the
        // outputs of any subcomponent
        std::vector<sole::uuid> subCompOutputForwards;

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

        const std::vector<sole::uuid> &getSubCompOutputForwards() const {
            return subCompOutputForwards;
        }

        void setSubCompOutputForwards(const std::vector<sole::uuid> &forwards) {
            TraceOutput::subCompOutputForwards = forwards;
        }


        template<class Archive>
        void serialize(Archive & archive)
        {
            archive( uuid, inputs, subCompOutputForwards );
        }
    };
}