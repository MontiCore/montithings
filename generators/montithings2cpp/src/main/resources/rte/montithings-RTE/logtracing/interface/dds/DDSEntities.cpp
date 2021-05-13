/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include <dds/DCPS/WaitSet.h>
#include "DDSEntities.h"
#include "ReqResMessageListener.h"

void DDSEntities::setInstanceName(std::string name) {

}

void DDSEntities::initMessageType() {
    responseTypeSupport = new DDSLogTracerMessage::ResponseTypeSupportImpl();
    requestTypeSupport = new DDSLogTracerMessage::RequestTypeSupportImpl();

    DDS::ReturnCode_t responseRegistration
            = responseTypeSupport->register_type(participant, RES_MESSAGE_TYPE);
    DDS::ReturnCode_t requestRegistration
            = requestTypeSupport->register_type(participant, REQ_MESSAGE_TYPE);
    if (responseRegistration != DDS::RETCODE_OK
        || requestRegistration != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | initMessageTypes failed!";
        exit(EXIT_FAILURE);
    }
}

void DDSEntities::initSubscriber() {
    subscriber = participant->create_subscriber(SUBSCRIBER_QOS_DEFAULT, nullptr,
                                                OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!subscriber) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | initSubscriber failed.";
        exit(EXIT_FAILURE);
    }
}

void DDSEntities::initPublisher() {
    publisher = participant->create_publisher(PUBLISHER_QOS_DEFAULT, nullptr,
                                              OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!publisher) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | initPublisher failed.";
        exit(EXIT_FAILURE);
    }
}

void DDSEntities::initTopic() {
    topicRequest = participant->create_topic(REQ_TOPIC, REQ_MESSAGE_TYPE, TOPIC_QOS_DEFAULT,
                                             nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    topicResponse = participant->create_topic(RES_TOPIC, RES_MESSAGE_TYPE, TOPIC_QOS_DEFAULT,
                                             nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!topicRequest || !topicResponse) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | initTopics failed!";
        exit(EXIT_FAILURE);
    }
}

bool DDSEntities::initParticipant(int argc, char **argv) {
    dpf = TheParticipantFactoryWithArgs (argc, argv);

    participant = dpf->create_participant(
            42, PARTICIPANT_QOS_DEFAULT, nullptr,
            OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!participant) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | createParticipant failed.";
        return false;
    }

    return true;
}

void DDSEntities::initRequestDataReader() {
    DDS::DataReaderListener_var listener(new ReqResMessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    subscriber->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReader = subscriber->create_datareader(
            topicRequest, dataReaderQos, listener, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReader) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | ERROR: initRequestDataReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    requestDataReader = DDSLogTracerMessage::RequestDataReader::_narrow(dataReader);

    if (!requestDataReader) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSEntities | ERROR: initRequestDataReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}

void DDSEntities::initResponseDataReader() {
    DDS::DataReaderListener_var listener(new ReqResMessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    subscriber->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReader = subscriber->create_datareader(
            topicResponse, dataReaderQos, listener, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReader) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | ERROR: initResponseDataReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    responseDataReader = DDSLogTracerMessage::ResponseDataReader::_narrow(dataReader);

    if (!responseDataReader) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSEntities | ERROR: initResponseDataReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}

void DDSEntities::initResponseDataWriter() {
    DDS::DataWriterQos dataWriterQoS;
    publisher->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataWriter_var dataWriter = publisher->create_datawriter(
            topicResponse, dataWriterQoS, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataWriter) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSEntities | ERROR: initResponseDataWriter() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    responseDataWriter = DDSLogTracerMessage::ResponseDataWriter::_narrow(dataWriter);

    if (!responseDataWriter) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSEntities | ERROR: initResponseDataWriter() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}

void DDSEntities::initRequestDataWriter() {
    DDS::DataWriterQos dataWriterQoS;
    publisher->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataWriter_var dataWriter = publisher->create_datawriter(
            topicRequest, dataWriterQoS, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataWriter) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSEntities | ERROR: initRequestDataWriter() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    requestDataWriter = DDSLogTracerMessage::RequestDataWriter::_narrow(dataWriter);

    if (!requestDataWriter) {
        CLOG (ERROR, DDS_LOG_ID)
                << "DDSEntities | ERROR: initRequestDataWriter() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}

void DDSEntities::send(DDSLogTracerMessage::Response res) {
    CLOG (INFO, DDS_LOG_ID) << "DDSEntities | sending response... ";

    DDS::ReturnCode_t error = responseDataWriter->write(res, DDS::HANDLE_NIL);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | send() write returned " << error;

    }
}

void DDSEntities::send(DDSLogTracerMessage::Request req) {
    CLOG (INFO, DDS_LOG_ID) << "DDSEntities | sending request... ";

    DDS::ReturnCode_t error = requestDataWriter->write(req, DDS::HANDLE_NIL);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | send() write returned " << error;

    }
}

void
DDSEntities::waitUntilReadersConnected(int number) {
    DDS::StatusCondition_var condition = requestDataWriter->get_statuscondition();
    condition->set_enabled_statuses(DDS::SUBSCRIPTION_MATCHED_STATUS);

    DDS::WaitSet_var ws = new DDS::WaitSet;
    ws->attach_condition(condition);

    while (true) {
        DDS::PublicationMatchedStatus matches{};
        if (requestDataWriter->get_publication_matched_status(matches) != DDS::RETCODE_OK) {
            CLOG (ERROR, DDS_LOG_ID) << "DDSEntities | subscription_matched_status failed!";
            exit(EXIT_FAILURE);
        }

        if (matches.current_count >= number) {
            break;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds (100));
        std::this_thread::yield();
    }

    ws->detach_condition(condition);
}


void DDSEntities::addResponseCallback(std::function<void(DDSLogTracerMessage::Response)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<ReqResMessageListener *> (responseDataReader->get_listener());
    listener->addOnResponseCallback(std::move(callback));
}

void DDSEntities::addRequestCallback(std::function<void(DDSLogTracerMessage::Request)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<ReqResMessageListener *> (requestDataReader->get_listener());
    listener->addOnRequestCallback(std::move(callback));
}

void
DDSEntities::cleanup() {
    participant->delete_contained_entities();
    dpf->delete_participant(participant);
    TheServiceParticipant->shutdown();
}
