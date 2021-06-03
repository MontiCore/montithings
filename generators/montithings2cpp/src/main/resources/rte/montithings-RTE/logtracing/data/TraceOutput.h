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

    template<class Archive>
    void serialize(Archive & archive)
    {
        archive( uuid, inputs );
    }
};
}