/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "DDSRecorder.h"
#include "VectorClock.h"

#include <utility>

void
DDSRecorder::init() {

    ddsCommunicator.setTopicName(topicName);
    // check if a ddsClient is present, as this is not the case with sensor actuator ports
    // instead, sensor actuator ports directly initialize the participant, no further action required at this point
    if (ddsClient) {
        // wait until participant is initialized
        while (!ddsClient->getParticipant()) {
            std::this_thread::sleep_for(std::chrono::milliseconds(10));
        }
        ddsCommunicator.setParticipant(ddsClient->getParticipant());
    }

    ddsCommunicator.initMessageTypes();
    ddsCommunicator.initTopics();
    ddsCommunicator.initPublisher();
    ddsCommunicator.initSubscriber();
    ddsCommunicator.initReaderCommandMessage();

    ddsCommunicator.initWriterAcknowledgement();
    ddsCommunicator.initWriterCommandReply();
    ddsCommunicator.initWriterRecorder();

    if (isOutgoingPort()) {
        ddsCommunicator.initReaderAcknowledgement();
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

void DDSRecorder::setDDSClient(DDSClient &client) {
    ddsClient = &client;
}

void
DDSRecorder::setTopicName(const std::string &name) {
    topicName = name;
    ddsCommunicator.setTopicName(name);
}

void
DDSRecorder::setPortName(const std::string &name) {
    portName = name;
}

void DDSRecorder::initParticipant(int argc, char *argv[]) {
    ddsCommunicator.initParticipant(argc, argv);
}

void
DDSRecorder::start() {
    CLOG (INFO, LOG_ID) << "DDSRecorder | starting recording... ";

    montithings::library::hwcinterceptor::startNondeterministicRecording();

    unsentMessageDelays.clear();
    unsentRecordMessageDelays.clear();
    unackedMessageTimestampMap.clear();
    unackedRecordedMessageTimestampMap.clear();
    
    ddsCommunicator.initWriterRecorder();

    // ddsClient is not present in sensor-actuator-ports
    if (ddsClient) {
        sendState(ddsClient->getSerializedState());
    }
}

void
DDSRecorder::stop() {
    CLOG (INFO, LOG_ID) << "DDSRecorder | stopping recording... ";
    montithings::library::hwcinterceptor::stopNondeterministicRecording();

    CLOG (INFO, LOG_ID) << "DDSRecorder | sending queue of unsent internal data (system calls, network delay, ...)";
    DDSMessage::Message emptyMessage;
    recordMessage(emptyMessage, "NOTOPIC", VectorClock::getVectorClock(), false);

    CLOG (INFO, LOG_ID) << "DDSRecorder | Cleaning up... ";
    ddsCommunicator.cleanupRecorderMessageWriter();
}

void
DDSRecorder::sendState(json state) {
    DDSRecorderMessage::Message recorderMessage;
    recorderMessage.id = recorderMessageId;
    recorderMessage.instance_name = instanceName.c_str();
    recorderMessage.type = DDSRecorderMessage::INTERNAL_STATE;
    recorderMessage.msg_content = state.dump().c_str();

    recorderMessageId++;

    CLOG (DEBUG, LOG_ID) << "DDSRecorder | sending internal state:" << recorderMessage.msg_content;
    ddsCommunicator.send(recorderMessage);
}

void
DDSRecorder::sendInternalRecords() {
    DDSRecorderMessage::Message recorderMessage;
    recorderMessage.id = recorderMessageId;
    recorderMessage.instance_name = instanceName.c_str();
    recorderMessage.type = DDSRecorderMessage::INTERNAL_RECORDS;

    nlohmann::json content;
    content["calls"] = montithings::library::hwcinterceptor::storageCalls;
    content["calc_latency"] = montithings::library::hwcinterceptor::storageComputationLatency;

    recorderMessage.msg_content = content.dump().c_str();

    // arbitrary msg_id
    recorderMessage.msg_id = 0;

    montithings::library::hwcinterceptor::storageCalls.clear();
    montithings::library::hwcinterceptor::storageComputationLatency.clear();

    recorderMessageId++;

    CLOG (DEBUG, LOG_ID) << "DDSRecorder | sending internal records:" << recorderMessage.msg_content;
    ddsCommunicator.send(recorderMessage);
}

bool
DDSRecorder::isOutgoingPort() {
    std::string sendingInstance = Util::Topic::getSendingInstanceNameFromTopic(topicName);
    return sendingInstance == instanceName;
}

void
DDSRecorder::recordMessage(DDSMessage::Message message, const char *topicName,
                           const vclock &newVectorClock, bool includeContent) {
    std::lock_guard<std::mutex> guard(sentMutex);

    if (montithings::library::hwcinterceptor::isRecording) {
        long long timestamp = Util::Time::getCurrentTimestampNano();

        // Only send ack if message was received, not sent
        if (!isOutgoingPort()) {
            std::string sendingInstance = Util::Topic::getSendingInstanceNameFromTopic(topicName);
            std::string pName = Util::Topic::getPortNameFromTopic(topicName);
            VectorClock::updateVectorClock(newVectorClock, sendingInstance);
            CLOG (DEBUG, LOG_ID) << "DDSRecorder | recordMessage | ACKing received message: message.id=" << message.id
                                 << " to=" << sendingInstance << " from=" << instanceName;
            ddsCommunicator.sendAck(sendingInstance, message.id, instanceName, pName,
                                    VectorClock::getSerializedVectorClock());
        } else {
            // message was sent and not received. Thus, add message to the map of unacked messages
            CLOG (DEBUG, LOG_ID) << "DDSRecorder | recordMessage | adding message with id=" << message.id
                                 << " to unackedMessageTimestampMap and unackedRecordedMessageTimestampMap";

            unackedMessageTimestampMap[message.id] = timestamp;
            unackedRecordedMessageTimestampMap[recorderMessageId] = timestamp;

            // Remove new lines from content
            std::string content{message.content.in()};
            content.erase(std::remove(content.begin(), content.end(), '\n'), content.end());

            nlohmann::json jUnsentDelays;
            jUnsentDelays["messages"] = unsentMessageDelays;
            unsentMessageDelays.clear();
            jUnsentDelays["record_messages"] = unsentRecordMessageDelays;
            unsentRecordMessageDelays.clear();

            DDSRecorderMessage::Message recorderMessage;
            recorderMessage.id = recorderMessageId;
            recorderMessage.instance_name = instanceName.c_str();
            recorderMessage.type = DDSRecorderMessage::MESSAGE_RECORD;
            recorderMessage.msg_id = message.id;
            recorderMessage.serialized_vector_clock = VectorClock::getSerializedVectorClock().c_str();
            recorderMessage.topic = topicName;
            recorderMessage.message_delays = jUnsentDelays.dump().c_str();

            recorderMessage.timestamp = timestamp;

            if (includeContent) {
                recorderMessage.msg_content = content.c_str();
            }

            CLOG (DEBUG, LOG_ID) << "DDSRecorder | sending to recorder: msg_id=" << recorderMessageId
                                 << " topic=" << topicName << " instance_name=" << instanceName.c_str()
                                 << " #delays (comp<->comp)=" << jUnsentDelays["messages"].size()
                                 << " #delays (comp<->comp)=" << jUnsentDelays["record_messages"].size();
            ddsCommunicator.send(recorderMessage);

            ++recorderMessageId;
        }

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
            // note that this functionality is not yet supported by the recorder.
            // nd calls are sent as soon as possible instead of on request
            sendInternalRecords();
            break;
        default:
            CLOG (ERROR, LOG_ID) << "DDSRecorder | onCommandMessage: unknown command";
    }
}

void
DDSRecorder::onAcknowledgementMessage(const DDSRecorderMessage::Acknowledgement &ack) {
    CLOG (DEBUG, LOG_ID) << "Received ACK | from=" << ack.sending_instance.in() << ", clock="
                         << ack.serialized_vector_clock.in() << ", receiving_instance=" << ack.receiving_instance.in()
                         << ", acked_id=" << ack.acked_id << ", portName=" << ack.port_name;

    std::string portIdentifier = portName + "/out";
    if (strcmp(ack.sending_instance.in(), "recorder") == 0 && strcmp(ack.port_name.in(), portIdentifier.c_str()) == 0) {
        handleAck(unackedRecordedMessageTimestampMap,
                  unsentRecordMessageDelays,
                  ack.sending_instance.in(),
                  ack.acked_id);

    } else if (isOutgoingPort() && strcmp(ack.port_name.in(), portIdentifier.c_str()) == 0) {
        handleAck(unackedMessageTimestampMap,
                  unsentMessageDelays,
                  ack.sending_instance.in(),
                  ack.acked_id);
    }
}

void
DDSRecorder::handleAck(unackedMap &unackedMap,
                       unsentDelayMap &unsentDelayMap,
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
    // NOTE: it would be more precise to adjust the delay base on the message size,
    // however, due to the OpenDDS layer the actual message size is abstraced and can only be guessed.
    // Instead, messages and ACKs are considered to be equal is size.
    long delay = (timestamp_ack_received - timestamp_sent) / 2;

    std::pair<std::string, long long> instanceDelay = std::make_pair(sendingInstance, delay);
    unsentDelayMap[ackedId] = instanceDelay;
}