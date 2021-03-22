/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <ace/OS_NS_stdlib.h>
#include "../../easyloggingpp/easylogging++.h"
#include <algorithm>
#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/Service_Participant.h>
#include <future>
#include <iostream>
#include <unordered_map>
#include <utility>

#include "../../json/json.hpp"
#include "../../tl/optional.hpp"
#include "../message-types/DDSMessageTypeSupportImpl.h"
#include "../message-types/DDSRecorderMessageTypeSupportImpl.h"
#include "DDSCommunicator.h"
#include "HWCInterceptor.h"
#include "VectorClock.h"
#include "utils.h"

#define LOG_ID "RECORDER"

class DDSRecorder {
private:
    std::mutex sentMutex;

    int messageId = 0;

    DDSCommunicator ddsCommunicator;
    std::string instanceName;
    std::string topicName;

    // key = <message id>, value = <sent timestamp>
    using unackedMap = std::unordered_map<long, long long>;
    unackedMap unackedMessageTimestampMap;
    unackedMap unackedRecordedMessageTimestampMap;

    // key = <message id>, value = { key = <port instance id>, value = <delay>}
    using unsentDelayMap = std::unordered_map<long, std::pair<std::string, long long>>;
    unsentDelayMap unsentMessageDelays;
    unsentDelayMap unsentRecordMessageDelays;

    static std::string getSendingInstanceNameFromTopic(const std::string topicId);

    bool isOutgoingPort();

    void start();

    void stop();

    void sendNDCalls(int commandId);

    void sendInternalRecords();

    void onCommandMessage(const DDSRecorderMessage::Command &message);

    void onAcknowledgementMessage(const DDSRecorderMessage::Acknowledgement &message);

    static void handleAck(unackedMap &unackedMap,
                          unsentDelayMap &unsentDelayMap,
                          const char *sendingInstance, long ackedId);

public:
    DDSRecorder() = default;

    ~DDSRecorder() = default;

    void init();

    void setInstanceName(const std::string &name);

    void setTopicName(const std::string &name);

    void recordMessage(DDSMessage::Message message, const char *topicName, const vclock &vectorClock, bool includeContent);
};
