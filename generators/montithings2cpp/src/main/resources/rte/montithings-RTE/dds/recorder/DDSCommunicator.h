/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <functional>
#include <future>
#include <iostream>
#include <string>
#include <vector>

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsPublicationC.h>

#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/Service_Participant.h>
#include <dds/DCPS/StaticIncludes.h>
#include <dds/DCPS/WaitSet.h>
#include <unordered_map>

#include "../message-types/DDSRecorderMessageTypeSupportImpl.h"
#include "Configurator.h"

using vclock = std::unordered_map<std::string, long>;

class DDSCommunicator : public Configurator {
private:
    // increasing message id for acknowledgements
    int ackId = 0;

public:
    DDSCommunicator() = default;

    ~DDSCommunicator() = default;

    void waitForRecorderReaders();

    void waitUntilCommandReadersConnected(int amount);

    void waitUntilRecorderWritersDisconnect();

    void cleanup();

    void cleanupPublisher();

    void cleanupRecorderMessageWriter();

    void cleanupCommandReplyMessageWriter();

    bool send(const DDSRecorderMessage::Message &message);

    bool send(const DDSRecorderMessage::Command &message);

    bool send(const DDSRecorderMessage::CommandReply &message);

    bool send(const DDSRecorderMessage::Acknowledgement &message);

    void sendAck(const std::string &sendingInstance, long ackedId, const std::string &receivedInstance,
                 const std::string &jVectorClock);

    bool commandWaitForAcks();

    bool commandReplyWaitForAcks();

    void addOnRecorderMessageCallback(std::function<void(DDSRecorderMessage::Message)> callback);

    void addOnCommandMessageCallback(std::function<void(DDSRecorderMessage::Command)> callback);

    void addOnCommandReplyMessageCallback(
            std::function<void(DDSRecorderMessage::CommandReply)> callback);

    void addOnAcknowledgementMessageCallback(
            std::function<void(DDSRecorderMessage::Acknowledgement)> callback);
};