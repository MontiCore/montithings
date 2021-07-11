/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <fstream>
#include <string>
#include <thread>
#include <vector>

#include <csignal>

#include "../montithings-RTE/json/json.hpp"
#include "lib/loguru.hpp"

#include "../montithings-RTE/record-and-replay/recorder/DDSCommunicator.h"
#include "../montithings-RTE/record-and-replay/recorder/utils.h"
#include "RecordProcessor.h"

class MessageFlowRecorder {
private:
    // maintains DDS entities such as the participant, subscriber, data readers, ...
    DDSCommunicator ddsCommunicator;

    DDS::DomainParticipant_var participant;

    bool isRecording = false;

    // amount of instances the recorder should wait for to connect until the actual recording is
    // started; can be overwritten by passing "-n"
    int instanceNumber = 1;

    // output file path for recordings
    std::string fileRecordingsPath;

    // stores all incoming messages unprocessed
    std::vector<DDSRecorderMessage::Message> recordedMessages;

    // storages for processed messages, non-deterministic calls, internal states, and computation latencies
    nlohmann::json storage;

    // simple counters which keep track of how many non-deterministic calls and computation latencies
    // have been received; only for logging purposes
    int statsCallsAmount = 0;
    int statsLatenciesAmount = 0;

    // spacing; overwritten by passing "--minSpacing"
    int minSpacing = 0;

    void onRecorderMessage(const DDSRecorderMessage::Message &message);

    void onCommandReplyMessage(const DDSRecorderMessage::CommandReply &message);

    void logProgress();

public:
    MessageFlowRecorder() = default;

    ~MessageFlowRecorder() = default;

    void setFileRecordings(std::string &filePath);

    void setDcpsInfoRepoHost(std::string &host);

    void setMinSpacing(int &spacing);

    void setInstanceNumber(int n);

    bool initDDSParticipant(int argc, char *argv[]);

    void init(int argc, char *argv[]);

    void start();

    void stop();

    void cleanup();

    void process();

    void saveToFile();
};