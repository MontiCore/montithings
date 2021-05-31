/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "LogEntry.h"

#include <utility>
namespace montithings {

LogEntry::LogEntry(long index, time_t time, std::string content, sole::uuid inputUuid,sole::uuid outputUuid)
        : index(index), time(time),  content(std::move(content)), inputUuid(inputUuid), outputUuid(outputUuid)
        {}

    long LogEntry::getIndex() const {
        return index;
    }

    void LogEntry::setIndex(long index) {
        LogEntry::index = index;
    }

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

const sole::uuid &LogEntry::getInputUuid() const {
    return inputUuid;
}

void LogEntry::setInputUuid(const sole::uuid &inputUuid) {
    LogEntry::inputUuid = inputUuid;
}

const sole::uuid &LogEntry::getOutputUuid() const {
    return outputUuid;
}

void LogEntry::setOutputUuid(const sole::uuid &outputUuid) {
    LogEntry::outputUuid = outputUuid;
}

}