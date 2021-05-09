/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "sole/sole.hpp"
namespace montithings {

class LogEntry {
private:
    time_t time;
    std::string content;

public:
    LogEntry(time_t time, std::string content);

    virtual ~LogEntry();

    time_t getTime() const;

    void setTime(time_t t);

    const std::string &getContent() const;

    void setContent(const std::string &c);
};
}