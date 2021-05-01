// (c) https://github.com/MontiCore/monticore
#pragma once

#include "../../easyloggingpp/easylogging++.h"

#include "../message-types/DDSRecorderMessageTypeSupportImpl.h"

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsPublicationC.h>

#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/Service_Participant.h>

#include <dds/DCPS/transport/framework/TransportRegistry.h>
#include <dds/DCPS/transport/framework/TransportConfig.h>
#include <dds/DCPS/transport/framework/TransportInst.h>
#include <dds/DCPS/transport/tcp/TcpInst.h>
#include <dds/DCPS/transport/tcp/Tcp.h>

#include "MessageListener.h"

#define DDS_LOG_ID "DDS"

using namespace OpenDDS::DCPS;

class Configurator {
private:
    TransportConfig_rch cfg;
    TransportInst_rch inst;

protected:
    const char *RECORDER_MESSAGE_TYPE = "Message Type";
    const char *RECORDER_MESSAGE_TOPIC = "Messages";
    const char *RECORDER_COMMAND_TYPE = "Command Type";
    const char *RECORDER_COMMAND_TOPIC = "Commands";
    const char *RECORDER_COMMANDREPLY_TYPE = "CommandReply Type";
    const char *RECORDER_COMMANDREPLY_TOPIC = "Command Replies";
    const char *RECORDER_ACKNOWLEDGE_TYPE = "Acknowledge Type";
    const char *RECORDER_ACKNOWLEDGE_TOPIC = "Acknowledgements";

    std::string dcpsInfoHost;
    std::string topicName;
    std::string instanceName;

    DDS::DomainParticipantFactory_var dpf;

    DDSRecorderMessage::MessageTypeSupport_var typeRecorderMessage;
    DDSRecorderMessage::CommandTypeSupport_var typeCommandMessage;
    DDSRecorderMessage::CommandReplyTypeSupport_var typeCommandReplyMessage;
    DDSRecorderMessage::AcknowledgementTypeSupport_var typeAcknowledgementMessage;

    DDS::DomainParticipant_var participant;
    DDSRecorderMessage::MessageDataReader_var readerRecorder;
    DDSRecorderMessage::CommandDataReader_var readerCommand;
    DDSRecorderMessage::CommandReplyDataReader_var readerCommandReply;
    DDSRecorderMessage::AcknowledgementDataReader_var readerAcknowledgement;

    DDSRecorderMessage::MessageDataWriter_var writerRecorder;
    DDSRecorderMessage::CommandDataWriter_var writerCommand;
    DDSRecorderMessage::CommandReplyDataWriter_var writerCommandReply;
    DDSRecorderMessage::AcknowledgementDataWriter_var writerAcknowledgement;

    DDS::Topic_var topicRecorder;
    DDS::Topic_var topicCommand;
    DDS::Topic_var topicCommandReply;
    DDS::Topic_var topicAcknowledgement;
    DDS::ContentFilteredTopic_var topicAcknowledgementFiltered;

    DDS::Subscriber_var subscriber;
    DDS::Publisher_var publisher;

public:
    Configurator() = default;

    ~Configurator() = default;

    void setDcpsInfoRepoHost(std::string host);

    void setTopicName(std::string name);

    void setInstanceName(std::string name);

    void setParticipant(const DDS::DomainParticipant_var& participant);

    void initConfig();

    void initMessageTypes();

    void initSubscriber();

    void initPublisher();

    void initTopics();

    void initParticipant(int argc, char *argv[]);

    void initReaderRecorderMessage();

    void initReaderCommandMessage();

    void initReaderCommandReplyMessage();

    void initReaderAcknowledgement();

    void initWriterRecorder();

    void initWriterCommand();

    void initWriterCommandReply();

    void initWriterAcknowledgement();
};
