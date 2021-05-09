/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogEntry.h"

#include <utility>
namespace montithings {

LogEntry::LogEntry(time_t time, std::string content)
        : time(time),  content(std::move(content)) {}

LogEntry::~LogEntry() = default;



time_t LogEntry::getTime() const {
    return time;
}

void LogEntry::setTime(time_t t) {
    LogEntry::time = t;
}

const std::string &LogEntry::getContent() const {
    return content;
}

void LogEntry::setContent(const std::string &c) {
    LogEntry::content = c;
}

}