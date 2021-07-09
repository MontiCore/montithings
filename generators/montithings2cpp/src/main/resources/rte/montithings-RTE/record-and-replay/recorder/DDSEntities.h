// (c) https://github.com/MontiCore/monticore

/**
 * Class dedicated for maintaining and instantiating DDS entities.
 * The recording module should not have many dependencies.
 * However, it instantiates its own subscriber and publisher, but uses the DDS participant of the DDSClient.
 * 
 * This was a design decision made after the performance evaluation showed significant overhead 
 * which might be caused by instantiating too many DDS participants.
 * Note, that each port instantiates a new recording module. 
 * 
 * Also note, that initialization of a new DDS participant is still supported and used by the recording tool.
 * 
 * Unfortunately, the way OpenDDS handles topics and corresponding data readers and writers lead to quite a lot boilerplate code.
 */

#pragma once

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsPublicationC.h>

#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/Service_Participant.h>

#include <dds/DCPS/transport/framework/TransportRegistry.h>
#include <dds/DCPS/transport/framework/TransportConfig.h>
#include <dds/DCPS/transport/framework/TransportInst.h>

// Supporting all transport types with a statically built OpenDDS
#include <dds/DCPS/transport/tcp/TcpInst.h>
#include <dds/DCPS/transport/tcp/Tcp.h>
#include <dds/DCPS/transport/rtps_udp/RtpsUdpInst.h>
#include <dds/DCPS/transport/rtps_udp/RtpsUdp.h>
#include <dds/DCPS/transport/shmem/ShmemInst.h>
#include <dds/DCPS/transport/shmem/Shmem.h>
#include <dds/DCPS/transport/udp/UdpInst.h>
#include <dds/DCPS/transport/udp/Udp.h>
#include <dds/DCPS/transport/multicast/MulticastInst.h>
#include <dds/DCPS/transport/multicast/Multicast.h>

#include "../../easyloggingpp/easylogging++.h"

#include "../message-types/DDSRecorderMessageTypeSupportImpl.h"
#include "MessageListener.h"
#include "DDSConstants.h"

#define DDS_LOG_ID "DDS_RECORDER"

using namespace OpenDDS::DCPS;

class DDSEntities : public DDSConstants {
private:
    TransportConfig_rch cfg;
    TransportInst_rch inst;

protected:
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
    DDSEntities() = default;

    ~DDSEntities() = default;

    void setDcpsInfoRepoHost(std::string host);

    void setTopicName(std::string name);

    void setInstanceName(std::string name);

    void initMessageTypes();

    void initSubscriber();

    void initPublisher();

    void initTopics();

    void initReaderRecorderMessage();

    void initReaderCommandMessage();

    void initReaderCommandReplyMessage();

    void initReaderAcknowledgement();

    void initWriterRecorder();

    void initWriterCommand();

    void initWriterCommandReply();

    void initWriterAcknowledgement();

    // The DDS participant can be initiated (done by the recording tool), 
    // or passed as an argument if a participant is already present, as done by components.
    void setParticipant(const DDS::DomainParticipant_var& participant);
    bool initParticipant(int argc, char *argv[]);
};
