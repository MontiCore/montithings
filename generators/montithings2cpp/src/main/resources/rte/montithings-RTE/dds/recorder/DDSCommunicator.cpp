/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "DDSCommunicator.h"
#include <unordered_map>
#include <utility>

void
DDSCommunicator::addOnCommandMessageCallback(
        std::function<void(DDSRecorderMessage::Command)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<MessageListener *> (readerCommand->get_listener());
    listener->addOnCommandMessageCallback(std::move(callback));
}

void
DDSCommunicator::addOnCommandReplyMessageCallback(
        std::function<void(DDSRecorderMessage::CommandReply)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<MessageListener *> (readerCommandReply->get_listener());
    listener->addOnCommandReplyMessageCallback(std::move(callback));
}

void
DDSCommunicator::addOnRecorderMessageCallback(
        std::function<void(DDSRecorderMessage::Message)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<MessageListener *> (readerRecorder->get_listener());
    listener->addOnRecorderMessageCallback(std::move(callback));
}

void
DDSCommunicator::addOnAcknowledgementMessageCallback(
        std::function<void(DDSRecorderMessage::Acknowledgement)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<MessageListener *> (readerAcknowledgement->get_listener());
    listener->addOnAcknowledgementMessageCallback(std::move(callback));
}

void
DDSCommunicator::waitUntilCommandReadersConnected(int amount) {
    DDS::StatusCondition_var condition = writerCommand->get_statuscondition();
    condition->set_enabled_statuses(DDS::SUBSCRIPTION_MATCHED_STATUS);

    DDS::WaitSet_var ws = new DDS::WaitSet;
    ws->attach_condition(condition);

    while (true) {
        DDS::PublicationMatchedStatus matches{};
        if (writerCommand->get_publication_matched_status(matches) != DDS::RETCODE_OK) {
            CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | subscription_matched_status failed!";
            exit(EXIT_FAILURE);
        }

        if (matches.current_count >= amount) {
            break;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds (100));
        std::this_thread::yield();
    }

    ws->detach_condition(condition);
}

// TODO refactor
void
DDSCommunicator::waitForRecorderReaders() {
    DDS::StatusCondition_var condition = writerRecorder->get_statuscondition();
    condition->set_enabled_statuses(DDS::SUBSCRIPTION_MATCHED_STATUS);

    DDS::WaitSet_var ws = new DDS::WaitSet;
    ws->attach_condition(condition);
    while (true) {
        DDS::PublicationMatchedStatus matches{};
        if (writerRecorder->get_publication_matched_status(matches) != DDS::RETCODE_OK) {
            CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | subscription_matched_status failed!";
            exit(1);
        }
        if (matches.current_count >= 2) {
            break;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds (100));
        std::this_thread::yield();
    }

    ws->detach_condition(condition);
}

void
DDSCommunicator::waitUntilRecorderWritersDisconnect() {
    DDS::StatusCondition_var condition = readerRecorder->get_statuscondition();
    condition->set_enabled_statuses(DDS::SUBSCRIPTION_MATCHED_STATUS);

    DDS::WaitSet_var ws = new DDS::WaitSet;
    ws->attach_condition(condition);
    while (true) {
        DDS::SubscriptionMatchedStatus matches{};
        if (readerRecorder->get_subscription_matched_status(matches) != DDS::RETCODE_OK) {
            CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | subscription_matched_status failed!";
            exit(1);
        }
        if (matches.current_count == 1) {
            break;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds (100));
        std::this_thread::yield();
    }

    ws->detach_condition(condition);
}

void
DDSCommunicator::cleanup() {
    participant->delete_contained_entities();
    dpf->delete_participant(participant);
    TheServiceParticipant->shutdown();
}

void
DDSCommunicator::cleanupRecorderMessageWriter() {
    if (!CORBA::is_nil(writerRecorder)) {
        publisher->delete_datawriter(writerRecorder);
    }
}

void
DDSCommunicator::cleanupCommandReplyMessageWriter() {
    if (!CORBA::is_nil(writerCommandReply)) {
        publisher->delete_datawriter(writerCommandReply);
    }
}

void
DDSCommunicator::cleanupPublisher() {
    publisher->delete_contained_entities();
    participant->delete_publisher(publisher);
}

bool
DDSCommunicator::send(const DDSRecorderMessage::Command &command) {
    if(!handleCommand) {
        handleCommand = writerCommand->register_instance(command);
    }
    DDS::ReturnCode_t error = writerCommand->write(command, handleCommand);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | send() write returned " << error;
        return false;
    }

    return true;
}

bool
DDSCommunicator::send(const DDSRecorderMessage::CommandReply &command) {
    if(!handleCommandReply) {
        handleCommandReply = writerCommandReply->register_instance(command);
    }

    DDS::ReturnCode_t error = writerCommandReply->write(command, handleCommandReply);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | send() write returned " << error;
        return false;
    }

    return true;
}

bool
DDSCommunicator::send(const DDSRecorderMessage::Message &message) {
    if(!handleRecordMessage) {
        handleRecordMessage = writerRecorder->register_instance(message);
    }

    DDS::ReturnCode_t error = writerRecorder->write(message, handleRecordMessage);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | send() write returned " << error;
        return false;
    }

    return true;
}

bool
DDSCommunicator::send(const DDSRecorderMessage::Acknowledgement &message) {
    if(!handleAcknowledgement) {
        handleAcknowledgement = writerAcknowledgement->register_instance(message);
    }

    DDS::ReturnCode_t error = writerAcknowledgement->write(message, handleAcknowledgement);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | send() write returned " << error;
        return false;
    }

    return true;
}

bool
DDSCommunicator::commandWaitForAcks() {
    DDS::PublicationMatchedStatus matches{};
    if (writerCommand->get_publication_matched_status(matches) == DDS::RETCODE_OK) {
        std::cout << "DDSCommunicator | commandWaitForAcks: " << matches.current_count << " listeners";
    }

    DDS::Duration_t timeout = {30, 0};
    if (writerCommand->wait_for_acknowledgments(timeout) != DDS::RETCODE_OK) {
        return false;
    }
    return true;
}

bool
DDSCommunicator::commandReplyWaitForAcks() {
    DDS::PublicationMatchedStatus matches{};
    if (writerCommandReply->get_publication_matched_status(matches) == DDS::RETCODE_OK) {
        // std::cout << "DDSCommunicator | commandWaitForAcks: " << matches.current_count << "
        // listeners";
    }

    DDS::Duration_t timeout = {30, 0};
    if (writerCommandReply->wait_for_acknowledgments(timeout) != DDS::RETCODE_OK) {
        return false;
    }
    return true;
}

void
DDSCommunicator::sendAck(const std::string &sendingInstance, long ackedId, const std::string &receivedInstance,
                         const std::string &pName, const std::string &jVectorClock) {
    DDSRecorderMessage::Acknowledgement ackMessage;
    ackMessage.sending_instance = receivedInstance.c_str();
    // receiving instance is now the previous sending instance
    ackMessage.receiving_instance = sendingInstance.c_str();
    ackMessage.acked_id = ackedId;
    ackMessage.serialized_vector_clock = jVectorClock.c_str();
    ackMessage.port_name = pName.c_str();
    send(ackMessage);
}