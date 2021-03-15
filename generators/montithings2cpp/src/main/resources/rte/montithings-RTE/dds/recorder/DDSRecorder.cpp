/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "DDSRecorder.h"

#include <utility>

void
DDSRecorder::init() {
    ddsCommunicator.setVerbose(false);
    ddsCommunicator.setPortIdentifier(portIdentifier);
    ddsCommunicator.initConfig();
    ddsCommunicator.initParticipant();
    ddsCommunicator.initMessageTypes();
    ddsCommunicator.initTopics();
    ddsCommunicator.initSubscriber();
    ddsCommunicator.initReaderCommandMessage();
    if (isOutgoingPort()) {
        ddsCommunicator.initReaderAcknowledgement(true);
        ddsCommunicator.addOnAcknowledgementMessageCallback(
                std::bind(&DDSRecorder::onAcknowledgementMessage, this, std::placeholders::_1));
    }

    ddsCommunicator.addOnCommandMessageCallback(
            std::bind(&DDSRecorder::onCommandMessage, this, std::placeholders::_1));
}

void
DDSRecorder::setInstanceName(const std::string &name) {
    instanceName = name;
    ddsCommunicator.setInstanceName(name);
}

void
DDSRecorder::setPortIdentifier(const std::string &name) {
    portIdentifier = name;
    ddsCommunicator.setPortIdentifier(name);
}

void
DDSRecorder::start() {
    CLOG (INFO, LOG_ID) << "DDSRecorder | starting recording... ";
    HWCInterceptor::startNondeterministicRecording();
    HWCInterceptor::storage["instance"] = instanceName;

    ddsCommunicator.initPublisher();
    ddsCommunicator.initWriter();
}

void
DDSRecorder::stop() {
    CLOG (INFO, LOG_ID) << "DDSRecorder | stopping recording... ";
    HWCInterceptor::stopNondeterministicRecording();
    ddsCommunicator.cleanupRecorderMessageWriter();
}

void
DDSRecorder::sendNDCalls(int commandId) {
    CLOG (DEBUG, LOG_ID) << "DDSRecorder | sendNDCalls";
    HWCInterceptor::storage["instance"] = instanceName;

    DDSRecorderMessage::CommandReply message;
    message.command_id = commandId;
    message.instance_name = instanceName.c_str();
    message.content = HWCInterceptor::storage.dump().c_str();
    message.id = 0;

    CLOG (DEBUG, LOG_ID) << "DDSRecorder | sendNDCalls:" << message.content;
    ddsCommunicator.send(message);

    // CLOG (DEBUG, LOG_ID) << "DDSRecorder | Waiting for ACKs ";
    // ddsCommunicator.commandReplyWaitForAcks();

    CLOG (DEBUG, LOG_ID) << "DDSRecorder | Cleaning up commandReply writers";
    ddsCommunicator.cleanupCommandReplyMessageWriter();
    CLOG (DEBUG, LOG_ID) << "DDSRecorder | Cleaning up commandReply writers done";
}

void
DDSRecorder::sendInternalRecords() {
    DDSRecorderMessage::Message recorderMessage;
    recorderMessage.id = messageId;
    recorderMessage.instance_name = instanceName.c_str();
    recorderMessage.type = DDSRecorderMessage::INTERNAL_RECORDS;

    nlohmann::json content;
    content["calls"] = HWCInterceptor::storageCalls;
    content["calc_latency"] = HWCInterceptor::storageComputationLatency;
    recorderMessage.msg_content = content.dump().c_str();

    // This can be dangerous...
    // TODO maybe add mutex
    HWCInterceptor::storageCalls.clear();
    HWCInterceptor::storageComputationLatency.clear();

    messageId++;

    CLOG (DEBUG, LOG_ID) << "DDSRecorder | sending internal records:" << recorderMessage.msg_content;
    ddsCommunicator.send(recorderMessage);
}

std::string
DDSRecorder::getSendingInstanceNameFromTopic(const std::string &topicId) {
    return topicId.substr(0, topicId.find_last_of('.'));
}

bool
DDSRecorder::isOutgoingPort() {
    std::string sendingInstance = getSendingInstanceNameFromTopic(portIdentifier);
    return sendingInstance == instanceName;
}

void
DDSRecorder::recordMessage(DDSMessage::Message message, char *topicName,
                           const std::unordered_map<std::string, long> &newVectorClock) {
    // CLOG (DEBUG, LOG_ID) << "DDSRecorder | recordMessage | Size of nd storage: " << Recorder::storage.size ()
    //;

    if (HWCInterceptor::isRecording) {
        long long timestamp = Util::Time::getCurrentTimestampNano();

        // Only send ack if message was received, not sent
        if (!isOutgoingPort()) {
            std::string sendingInstance = getSendingInstanceNameFromTopic(topicName);
            updateVectorClock(newVectorClock, sendingInstance);
            CLOG (DEBUG, LOG_ID) << "DDSRecorder | recordMessage | ACKing received message: message.id=" << message.id
                                 << "port=" << portIdentifier << "clock=" << getSerializedVectorClock;
            ddsCommunicator.sendAck(sendingInstance, message.id, portIdentifier, getSerializedVectorClock());
        } else {
            // message was sent and not received. Thus, add message to the map of unacked messages
            CLOG (DEBUG, LOG_ID) << "DDSRecorder | recordMessage | adding message with id=" << message.id
                                 << " to unackedMessageTimestampMap and unackedRecordedMessageTimestampMap";
            unackedMessageTimestampMap[message.id] = timestamp;
            unackedRecordedMessageTimestampMap[messageId] = timestamp;
        }

        // Remove new lines from content
        std::string content{message.content.in()};
        content.erase(std::remove(content.begin(), content.end(), '\n'), content.end());

        nlohmann::json jUnsentDelays;
        jUnsentDelays["messages"] = unsentMessageDelays;
        unsentMessageDelays.clear();
        jUnsentDelays["record_messages"] = unsentRecordMessageDelays;
        unsentRecordMessageDelays.clear();

        DDSRecorderMessage::Message recorderMessage;
        recorderMessage.id = messageId;
        recorderMessage.instance_name = instanceName.c_str();
        recorderMessage.type = DDSRecorderMessage::MESSAGE_RECORD;
        recorderMessage.msg_id = message.id;
        recorderMessage.msg_content = content.c_str();
        recorderMessage.timestamp = timestamp;
        std::string topic{topicName};
        recorderMessage.topic = topic.c_str();
        recorderMessage.message_delays = jUnsentDelays.dump().c_str();

        CLOG (DEBUG, LOG_ID) << "DDSRecorder | sending to recorder: msg_id=" << messageId
                             << " #delays (comp<->comp)=" << jUnsentDelays["messages"].size()
                             << " #delays (comp<->comp)=" << jUnsentDelays["record_messages"].size();
        ddsCommunicator.send(recorderMessage);

        ++messageId;

        sendInternalRecords();
    }
}

void
DDSRecorder::onCommandMessage(const DDSRecorderMessage::Command &command) {
    switch (command.cmd) {
        case DDSRecorderMessage::RECORDING_START:
            start();
            break;
        case DDSRecorderMessage::RECORDING_STOP:
            stop();
            break;
        case DDSRecorderMessage::SEND_INTERNAL_ND_CALLS:
            sendNDCalls(command.id);
            break;
        default:
            CLOG (ERROR, LOG_ID) << "DDSRecorder | onCommandMessage: unknown command";
    }
}

void
DDSRecorder::onAcknowledgementMessage(const DDSRecorderMessage::Acknowledgement &ack) {
    CLOG (DEBUG, LOG_ID) << "Received ACK | id=" << ack.id << ", from=" << ack.sending_instance.in() << ", clock="
                         << ack.serialized_vector_clock.in() << ", receiving_instance=" << ack.receiving_instance.in()
                         << ", acked_id=" << ack.acked_id;

    if (strcmp(ack.sending_instance.in(), "recorder") == 0) {
        handleAck(unackedRecordedMessageTimestampMap, unsentRecordMessageDelays, "recorder", ack.acked_id);
    } else if (isOutgoingPort()) {
        handleAck(unackedMessageTimestampMap, unsentMessageDelays, ack.sending_instance.in(), ack.acked_id);
    }
}

void
DDSRecorder::handleAck(std::unordered_map<long, long long> &unackedMap,
                       std::unordered_map<long, long long> &unsentDelayMap,
                       const char *sendingInstance, long ackedId) {
    long long timestamp_ack_received = Util::Time::getCurrentTimestampNano();

    if (unackedMap.count(ackedId) == 0) {
        CLOG (ERROR, LOG_ID) << "handleAck | no entry found for acked_id " << ackedId
                             << " in unackedMessageTimestampMap!";
        for (auto x : unackedMap) {
            CLOG (DEBUG, LOG_ID) << x.first << " " << x.second;
        }
        return;
    }

    long long timestamp_sent = unackedMap[ackedId];
    // TODO: change by message size
    long delay = (timestamp_ack_received - timestamp_sent) / 2;

    unsentDelayMap[ackedId] = delay;

    // Delete entry

    //unackedMap.erase(ackedId);
}