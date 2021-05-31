/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "sole/sole.hpp"

namespace montithings {

class LogEntry {
private:
    long index;
    time_t time;
    std::string content;
    sole::uuid inputUuid;
    sole::uuid outputUuid;

public:
    LogEntry(long index, time_t time, std::string content, sole::uuid inputUuid, sole::uuid outputUuid);

    virtual ~LogEntry();

    long getIndex() const;

    void setIndex(long index);

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
        archive( index, time, content, inputUuid, outputUuid );
    }
};
}