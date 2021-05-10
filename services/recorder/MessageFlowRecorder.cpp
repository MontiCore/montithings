/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "MessageFlowRecorder.h"

// ignore warnings about endless loops
#pragma clang diagnostic push
#pragma ide diagnostic ignored "EndlessLoop"

void
MessageFlowRecorder::init(int argc, char *argv[]) {
    while (!ddsCommunicator.initParticipant(argc, argv)) {
        LOG_F (ERROR, "Is the DCPSInfoRepo service running or multicast enabled/allowed? Trying again...");
        std::this_thread::sleep_for(std::chrono::seconds(1));
    }

    ddsCommunicator.initMessageTypes();
    ddsCommunicator.initTopics();
    ddsCommunicator.initSubscriber();
    ddsCommunicator.initPublisher();

    ddsCommunicator.initWriterAcknowledgement();
    ddsCommunicator.initWriterCommand();
    ddsCommunicator.initWriterCommandReply();
    ddsCommunicator.initWriterRecorder();

    ddsCommunicator.initReaderRecorderMessage();
    ddsCommunicator.initReaderCommandReplyMessage();

    ddsCommunicator.setTopicName("recorder");

    ddsCommunicator.addOnRecorderMessageCallback(
            std::bind(&MessageFlowRecorder::onRecorderMessage, this, std::placeholders::_1));
    ddsCommunicator.addOnCommandReplyMessageCallback(
            std::bind(&MessageFlowRecorder::onCommandReplyMessage, this, std::placeholders::_1));

}

bool MessageFlowRecorder::initDDSParticipant(int argc, char *argv[]) {
    DDS::DomainParticipantFactory_var dpf = TheParticipantFactoryWithArgs(argc, argv);
    // We do not make use of multiple DDS domains yet, arbitrary but fixed id will do it
    int domainId = 42;
    if (!participant) {
        participant = dpf->create_participant(domainId, PARTICIPANT_QOS_DEFAULT, 0, OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    }

    if (!participant) {
        LOG_F (ERROR, "Is the DCPSInfoRepo service running or multicast enabled/allowed? Trying again...");
        return false;
    }

    return true;
}

void
MessageFlowRecorder::setFileRecordings(std::string &filePath) {
    this->fileRecordingsPath = filePath;
}

void
MessageFlowRecorder::setDcpsInfoRepoHost(std::string &host) {
    ddsCommunicator.setDcpsInfoRepoHost(host);
}

void
MessageFlowRecorder::setInstanceNumber(int n) {
    instanceNumber = n;
}

void
MessageFlowRecorder::setMinSpacing(int &spacing) {
    minSpacing = spacing;
}

void
MessageFlowRecorder::start() {
    storage = nlohmann::json::object();
    statsCallsAmount = 0;
    statsLatenciesAmount = 0;

    LOG_F (INFO, "Waiting until application is started... ");
    ddsCommunicator.waitUntilCommandReadersConnected(instanceNumber);
    if (instanceNumber == 1) {
        LOG_F (INFO, "At least one entity listens for commands. Waiting 2 seconds for others.");
        std::this_thread::sleep_for(std::chrono::seconds(2));
        LOG_F (INFO, "We can probably start.");
    } else {
        LOG_F (INFO, "%d entities listen for commands. We can start.", instanceNumber);
    }

    // starts thread which logs the current amount of recorded data each 5 seconds
    std::thread logger(&MessageFlowRecorder::logProgress, this);
    logger.detach();

    LOG_F (INFO, "Sending command: RECORDING_START");
    DDSRecorderMessage::Command startCommand;
    startCommand.instance_name = "recorder";
    startCommand.cmd = DDSRecorderMessage::RECORDING_START;
    ddsCommunicator.send(startCommand);

    LOG_F (INFO, "Waiting until all participants acknowledge the RECORDING_START command...");
    if (!ddsCommunicator.commandWaitForAcks()) {
        LOG_F (ERROR, "RECORDING_START command was not ACKed by all components, stopping.");
        stop();
    }
    LOG_F (INFO, "RECORDING_START command was ACKed by participants.");

    LOG_F (INFO, "Recording...");
    isRecording = true;

    // recording is done event-based
    while (isRecording) {
        std::this_thread::sleep_for(std::chrono::milliseconds(10));
        std::this_thread::yield();
    }

    cleanup();
    process();
    saveToFile();
}

void
MessageFlowRecorder::stop() {
    LOG_F (INFO, "Stopping...");
    if (!isRecording) {
        LOG_F (INFO, "Recording did not start, yet. There is nothing to do, exiting...");
        cleanup();
        exit(EXIT_SUCCESS);
    }
    isRecording = false;
    LOG_SCOPE_F (INFO, "Sending command: RECORDING_STOP");
    DDSRecorderMessage::Command stopCommand;
    stopCommand.cmd = DDSRecorderMessage::RECORDING_STOP;
    ddsCommunicator.send(stopCommand);


    LOG_F (INFO, "Waiting for ACKs...");
    if (!ddsCommunicator.commandWaitForAcks()) {
        LOG_F (ERROR, "RECORDING_STOP command was not ACKed by all components, exiting.");
        cleanup();
        exit(1);
    }
    LOG_F (INFO, "RECORDING_STOP command was ACKed by all subscribers.");

    LOG_F (INFO, "Waiting until all participants disconnect...");
    ddsCommunicator.waitUntilRecorderWritersDisconnect();
    LOG_F (INFO, "All participants disconnected.");
}

void
MessageFlowRecorder::process() {
    RecordProcessor processor;
    if (!recordedMessages.empty()) {
        storage["recordings"] = processor.process(recordedMessages, minSpacing);
    }
}

void
MessageFlowRecorder::cleanup() {
    ddsCommunicator.cleanup();
}

void
MessageFlowRecorder::saveToFile() {
    LOG_F (INFO, "Flushing recorded data into file %s.", fileRecordingsPath.c_str());
    std::ofstream outfile;
    outfile.open(fileRecordingsPath);
    outfile << storage.dump();
    outfile.close();
}

void
MessageFlowRecorder::onRecorderMessage(const DDSRecorderMessage::Message &message) {
    LOG_F (1, "onRecorderMessage: id=%d,instance=%s,content=%s,msg_id=%d,ts=%ld,topic=%s,delays=%s.", message.id,
           message.instance_name.in(),
           message.msg_content.in(), message.msg_id, message.timestamp, message.topic.in(),
           message.message_delays.in());

    DDSRecorderMessage::Message toStore = message;
    std::string pName = Util::Topic::getPortNameFromTopic(message.topic.in());

    switch (message.type) {
        case DDSRecorderMessage::MESSAGE_RECORD:
            LOG_F (1, "ACKing: instance_name=%s, id=%d, port=%s,", message.instance_name.in(), message.id,
                   pName.c_str());
            if(strcmp(message.topic.in(), "NOTOPIC") != 0 ){
                ddsCommunicator.sendAck(message.instance_name.in(), message.id, "recorder", pName, "");
            }

            // Replace timestamp by recorder clock readings
            toStore.timestamp = Util::Time::getCurrentTimestampNano();

            // store message unprocessed and move on, dont waste time
            recordedMessages.push_back(toStore);
            break;
        case DDSRecorderMessage::INTERNAL_STATE: {
            nlohmann::json state = nlohmann::json::parse(message.msg_content.in());
            std::string instance = message.instance_name.in();
            storage["states"][instance] = state;
            LOG_F (1, "Received internal state from %s.", instance.c_str());

            break;
        }
        case DDSRecorderMessage::INTERNAL_RECORDS: {
            nlohmann::json content = nlohmann::json::parse(message.msg_content.in());
            std::string instance = message.instance_name.in();

            for (auto call : content["calls"]) {
                std::string callId = call[0].dump();

                storage["calls"][instance][callId] = call[1];
                statsCallsAmount++;
            }

            for (auto latency : content["calc_latency"]) {
                std::string latencyId = latency[0].dump();
                storage["computation_latency"][instance][latencyId] = latency[1];
                statsLatenciesAmount++;
            }

            LOG_F (1,
                   "Received internal recording data from %s: %ld system calls intercepted and %ld "
                   "computation latencies measured.",
                   instance.c_str(), content["calls"].size(), content["calc_latency"].size());
            break;
        }
        default:
            std::cerr << "DDSRecorder | onRecorderMessage: unknown message type" << std::endl;
    }
}

void
MessageFlowRecorder::onCommandReplyMessage(const DDSRecorderMessage::CommandReply &message) {
    LOG_F (1, "onCommandReplyMessage:content=%s,command_id=%d.", message.content.in(),
           message.command_id);
}

void
MessageFlowRecorder::logProgress() {
    while (true) {
        std::this_thread::sleep_for(std::chrono::seconds(5));

        if (isRecording) {
            LOG_F (INFO, "Messages: %lu, Calls: %u, Computation Latencies: %u",
                   recordedMessages.size(), statsCallsAmount, statsLatenciesAmount);
        }
    }
}

#pragma clang diagnostic pop