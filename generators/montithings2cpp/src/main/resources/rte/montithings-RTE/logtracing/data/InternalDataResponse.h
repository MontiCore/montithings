/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "sole/sole.hpp"

namespace montithings {

class LogEntry {
private:
    time_t time;
    std::string content;
    sole::uuid inputUuid;
    sole::uuid outputUuid;

public:
    LogEntry(time_t time, std::string content, sole::uuid inputUuid, sole::uuid outputUuid);

    virtual ~LogEntry();

    time_t getTime() const;

    void setTime(time_t t);

    const std::string &getContent() const;

    void setContent(const std::string &c);

    const sole::uuid &getInputUuid() const;

    void setInputUuid(const sole::uuid &inputUuid);

    const sole::uuid &getOutputUuid() const;

    void setOutputUuid(const sole::uuid &outputUuid);

    template<class Archive>
    void serialize(Archive & archive)
    {
        archive( time, content, inputUuid, outputUuid );
    }
};
}