/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include <chrono>
#include "sole/sole.hpp"

namespace montithings {

    class LogEntry {
    private:
        sole::uuid uuid{};
        long index;

        // components may lack of log statements
        // in this case virtual log statements are generated in order to provide tracing via inputs/outputs
        long indexSecondary;

        time_t time;
        std::string content;
        sole::uuid inputUuid;
        sole::uuid outputUuid;

    public:
        LogEntry(long index, time_t time, std::string content, sole::uuid inputUuid, sole::uuid outputUuid)
                : index(index), time(time), content(std::move(content)), inputUuid(inputUuid), outputUuid(outputUuid) {
            uuid = sole::uuid4();
            indexSecondary = 0;
        }

        ~LogEntry() = default;

        const sole::uuid &getUuid() const {
            return uuid;
        }

        void setUuid(const sole::uuid &id) {
            uuid = id;
        }

        long getIndex() const {
            return index;
        }

        void setIndex(long i) {
            index = i;
        }

        long getIndexSecondary() const {
            return indexSecondary;
        }

        void setIndexSecondary(long i2) {
            LogEntry::indexSecondary = i2;
        }

        time_t getTime() const {
            return time;
        }

        void setTime(time_t t) {
            time = t;
        }

        const std::string &getContent() const {
            return content;
        }

        void setContent(const std::string &c) {
            content = c;
        }

        const sole::uuid &getInputUuid() const {
            return inputUuid;
        }

        void setInputUuid(const sole::uuid &inUuid) {
            inputUuid = inUuid;
        }

        const sole::uuid &getOutputUuid() const {
            return outputUuid;
        }

        void setOutputUuid(const sole::uuid &outUuid) {
            outputUuid = outUuid;
        }

        template<class Archive>
        void serialize(Archive & archive)
        {
            archive( uuid, index, indexSecondary, time, content, inputUuid, outputUuid );
        }
    };
}