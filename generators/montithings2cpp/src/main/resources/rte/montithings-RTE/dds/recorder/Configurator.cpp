/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "Configurator.h"

void
Configurator::setDcpsInfoRepoHost(std::string host) {
    this->dcpsInfoHost = std::move(host);
}

void
Configurator::setTopicName(std::string name) {
    topicName = std::move(name);
}

void
Configurator::setInstanceName(std::string name) {
    instanceName = std::move(name);
}

void
Configurator::initMessageTypes() {
    typeRecorderMessage = new DDSRecorderMessage::MessageTypeSupportImpl();
    typeCommandMessage = new DDSRecorderMessage::CommandTypeSupportImpl();
    typeCommandReplyMessage = new DDSRecorderMessage::CommandReplyTypeSupportImpl();
    typeAcknowledgementMessage = new DDSRecorderMessage::AcknowledgementTypeSupportImpl();
    DDS::ReturnCode_t recorderRegistration
            = typeRecorderMessage->register_type(participant, RECORDER_MESSAGE_TYPE);
    DDS::ReturnCode_t commandRegistration
            = typeCommandMessage->register_type(participant, RECORDER_COMMAND_TYPE);
    DDS::ReturnCode_t commandReplyRegistration
            = typeCommandReplyMessage->register_type(participant, RECORDER_COMMANDREPLY_TYPE);
    DDS::ReturnCode_t acknowledgementRegistration
            = typeAcknowledgementMessage->register_type(participant, RECORDER_ACKNOWLEDGE_TYPE);

    if (recorderRegistration != DDS::RETCODE_OK
        || commandRegistration != DDS::RETCODE_OK
        || commandReplyRegistration != DDS::RETCODE_OK
        || acknowledgementRegistration != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | initMessageTypes failed!";
        exit(EXIT_FAILURE);
    }
}


void Configurator::setParticipant(const DDS::DomainParticipant_var& p) {
    participant = p;
}

bool
Configurator::initParticipant(int argc, char *argv[]) {
    // Initialize DomainParticipantFactory
    dpf = TheParticipantFactoryWithArgs (argc, argv);

    // Create DomainParticipant
    participant = dpf->create_participant(
            42, PARTICIPANT_QOS_DEFAULT, nullptr,
            OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!participant) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | createParticipant failed.";
        return false;
    }

    return true;
}

void
Configurator::initTopics() {
    topicRecorder = participant->create_topic(RECORDER_MESSAGE_TOPIC,
            // Topics are type-specific
                                              RECORDER_MESSAGE_TYPE,
            // QoS includes KEEP_LAST_HISTORY_QOS which might be
            // changed when log traces are inspected
                                              TOPIC_QOS_DEFAULT,
            // no topic listener required
                                              nullptr,
            // default status mask ensures that
            // all relevant communication status
            // changes are communicated to the
            // application
                                              OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    topicCommand
            = participant->create_topic(RECORDER_COMMAND_TOPIC, RECORDER_COMMAND_TYPE, TOPIC_QOS_DEFAULT,
                                        nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    topicCommandReply
            = participant->create_topic(RECORDER_COMMANDREPLY_TOPIC, RECORDER_COMMANDREPLY_TYPE,
                                        TOPIC_QOS_DEFAULT, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    topicAcknowledgement
            = participant->create_topic(RECORDER_ACKNOWLEDGE_TOPIC, RECORDER_ACKNOWLEDGE_TYPE,
                                        TOPIC_QOS_DEFAULT, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    std::string topicNameFiltered(RECORDER_ACKNOWLEDGE_TOPIC);
    topicNameFiltered.append("-filtered-");
    topicNameFiltered.append(topicName);

    DDS::StringSeq topicfiltered_params(1);
    topicfiltered_params.length(1);
    topicfiltered_params[0] = instanceName.c_str();

    topicAcknowledgementFiltered = participant->create_contentfilteredtopic(
            topicNameFiltered.c_str(), topicAcknowledgement,
            "(receiving_instance = %0)",
            topicfiltered_params);

    if (!topicRecorder || !topicCommand || !topicCommandReply || !topicAcknowledgement
        || !topicAcknowledgementFiltered) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | initTopics failed!";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initSubscriber() {
    subscriber = participant->create_subscriber(SUBSCRIBER_QOS_DEFAULT, nullptr,
                                                OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!subscriber) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | initSubscriber failed.";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initPublisher() {
    publisher = participant->create_publisher(PUBLISHER_QOS_DEFAULT, nullptr,
                                              OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!publisher) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | initPublisher failed.";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initReaderRecorderMessage() {
    DDS::DataReaderListener_var listener(new MessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    subscriber->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReaderRecorder = subscriber->create_datareader(
            topicRecorder, dataReaderQos, listener, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReaderRecorder) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | ERROR: initReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    readerRecorder = DDSRecorderMessage::MessageDataReader::_narrow(dataReaderRecorder);

    if (!readerRecorder) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initReaderAcknowledgement() {
    DDS::DataReaderListener_var listener(new MessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    subscriber->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReaderAcknowledgement
            = subscriber->create_datareader(topicAcknowledgementFiltered, dataReaderQos, listener,
                                            OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReaderAcknowledgement) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | ERROR: initReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    readerAcknowledgement
            = DDSRecorderMessage::AcknowledgementDataReader::_narrow(dataReaderAcknowledgement);

    if (!readerAcknowledgement) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initReaderCommandMessage() {
    DDS::DataReaderListener_var listener(new MessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    subscriber->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReaderCommand
            = subscriber->create_datareader(topicCommand, dataReaderQos, listener,
                    // default status mask ensures that
                    // all relevant communication status
                    // changes are communicated to the
                    // application
                                            OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReaderCommand) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | ERROR: initReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    readerCommand = DDSRecorderMessage::CommandDataReader::_narrow(dataReaderCommand);

    if (!readerCommand) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initReaderCommandReplyMessage() {
    DDS::DataReaderListener_var listener(new MessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    subscriber->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReaderCommandReply = subscriber->create_datareader(
            topicCommandReply, dataReaderQos, listener, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReaderCommandReply) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | ERROR: initReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    readerCommandReply = DDSRecorderMessage::CommandReplyDataReader::_narrow(dataReaderCommandReply);

    if (!readerCommandReply) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}


void
Configurator::initWriterRecorder() {
    DDS::DataWriterQos dataWriterQoS;
    publisher->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataWriter_var dataWriterRecorder = publisher->create_datawriter(
            topicRecorder, dataWriterQoS, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataWriterRecorder) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initWriterRecorder() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    writerRecorder = DDSRecorderMessage::MessageDataWriter::_narrow(dataWriterRecorder);

    if (!writerRecorder) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initWriterRecorder() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initWriterCommand() {
    DDS::DataWriterQos dataWriterQoS;
    publisher->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.history.kind = DDS::KEEP_ALL_HISTORY_QOS;
    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;
    dataWriterQoS.durability.kind = DDS::TRANSIENT_DURABILITY_QOS;

    DDS::DataWriter_var dataWriterCommand
            = publisher->create_datawriter(topicCommand, dataWriterQoS,
                    // no listener required
                                           nullptr,
                    // default status mask ensures that
                    // all relevant communication status
                    // changes are communicated to the
                    // application
                                           OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataWriterCommand) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initWriterCommand() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    writerCommand = DDSRecorderMessage::CommandDataWriter::_narrow(dataWriterCommand);

    if (!writerCommand) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initWriterCommand() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initWriterCommandReply() {
    DDS::DataWriterQos dataWriterQoS;
    publisher->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataWriter_var dataWriterCommandReply = publisher->create_datawriter(
            topicCommandReply, dataWriterQoS, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);


    if (!dataWriterCommandReply) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initWriterCommandReply() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    writerCommandReply = DDSRecorderMessage::CommandReplyDataWriter::_narrow(dataWriterCommandReply);

    if (!writerCommandReply) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSCommunicator | ERROR: initWriterCommandReply() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}

void
Configurator::initWriterAcknowledgement() {
    DDS::DataWriterQos dataWriterQoS;
    publisher->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataWriter_var dataWriterAcknowledge = publisher->create_datawriter(
            topicAcknowledgement, dataWriterQoS, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataWriterAcknowledge) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | ERROR: initWriterAcknowledgement() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    writerAcknowledgement = DDSRecorderMessage::AcknowledgementDataWriter::_narrow(dataWriterAcknowledge);

    if (!writerAcknowledgement) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSCommunicator | ERROR: initWriterAcknowledgement() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}