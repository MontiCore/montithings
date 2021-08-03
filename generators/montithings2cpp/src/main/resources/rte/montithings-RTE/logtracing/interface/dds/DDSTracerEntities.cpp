/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include "DDSTracerEntities.h"

void DDSTracerEntities::setInstanceName(std::string name) {
    instanceName = name;
}

void DDSTracerEntities::initMessageType() {
    responseTypeSupport = new DDSLogTracerMessage::ResponseTypeSupportImpl();
    requestTypeSupport = new DDSLogTracerMessage::RequestTypeSupportImpl();

    DDS::ReturnCode_t responseRegistration
            = responseTypeSupport->register_type(ddsClient->getParticipant(), RES_MESSAGE_TYPE);
    DDS::ReturnCode_t requestRegistration
            = requestTypeSupport->register_type(ddsClient->getParticipant(), REQ_MESSAGE_TYPE);
    if (responseRegistration != DDS::RETCODE_OK
        || requestRegistration != DDS::RETCODE_OK) {
        CLOG (ERROR, LOGTRACER_LOG_ID) << "DDSTracerEntities | initMessageTypes failed!";
        exit(EXIT_FAILURE);
    }
}



void DDSTracerEntities::initTopic() {
    topicRequest = ddsClient->getParticipant()->create_topic(REQ_TOPIC, REQ_MESSAGE_TYPE, TOPIC_QOS_DEFAULT,
                                                             nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    topicResponse = ddsClient->getParticipant()->create_topic(RES_TOPIC, RES_MESSAGE_TYPE, TOPIC_QOS_DEFAULT,
                                                              nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    
    // Filtered topics so that requests are only sent to the target instance.
    std::string topicRequestFilteredName(REQ_TOPIC);
    topicRequestFilteredName.append("-filtered-");
    topicRequestFilteredName.append(instanceName);

    DDS::StringSeq topicfiltered_params(1);
    topicfiltered_params.length(1);
    topicfiltered_params[0] = instanceName.c_str();

    topicRequestFiltered = ddsClient->getParticipant()->create_contentfilteredtopic(
            topicRequestFilteredName.c_str(), topicRequest,
            "target_instance = %0",
            topicfiltered_params);

    if (!topicRequestFiltered || !topicResponse) {
        CLOG (ERROR, LOGTRACER_LOG_ID) << "DDSTracerEntities | initTopics failed!";
        exit(EXIT_FAILURE);
    }
}


void DDSTracerEntities::initRequestDataReader() {
    DDS::DataReaderListener_var listener(new ReqResMessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    ddsClient->getSubscriber()->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReader = ddsClient->getSubscriber()->create_datareader(
            topicRequestFiltered, dataReaderQos, listener, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReader) {
        CLOG (ERROR, LOGTRACER_LOG_ID) << "DDSTracerEntities | ERROR: initRequestDataReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    requestDataReader = DDSLogTracerMessage::RequestDataReader::_narrow(dataReader);

    if (!requestDataReader) {
        CLOG (ERROR, LOGTRACER_LOG_ID)
                << "DDSTracerEntities | ERROR: initRequestDataReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}

void DDSTracerEntities::initResponseDataReader() {
    DDS::DataReaderListener_var listener(new ReqResMessageListener());
    // Definitions of the QoS settings
    DDS::DataReaderQos dataReaderQos;

    // Applies default qos settings
    ddsClient->getSubscriber()->get_default_datareader_qos(dataReaderQos);
    dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataReader_var dataReader = ddsClient->getSubscriber()->create_datareader(
            topicResponse, dataReaderQos, listener, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataReader) {
        CLOG (ERROR, LOGTRACER_LOG_ID) << "DDSTracerEntities | ERROR: initResponseDataReader() - OpenDDS data reader creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    responseDataReader = DDSLogTracerMessage::ResponseDataReader::_narrow(dataReader);

    if (!responseDataReader) {
        CLOG (ERROR, LOGTRACER_LOG_ID)
                << "DDSTracerEntities | ERROR: initResponseDataReader() - OpenDDS message reader narrowing failed.";
        exit(EXIT_FAILURE);
    }
}

void DDSTracerEntities::initResponseDataWriter() {
    DDS::DataWriterQos dataWriterQoS;
    ddsClient->getPublisher()->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataWriter_var dataWriter = ddsClient->getPublisher()->create_datawriter(
            topicResponse, dataWriterQoS, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataWriter) {
        CLOG (ERROR, LOGTRACER_LOG_ID)
                << "DDSTracerEntities | ERROR: initResponseDataWriter() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    responseDataWriter = DDSLogTracerMessage::ResponseDataWriter::_narrow(dataWriter);

    if (!responseDataWriter) {
        CLOG (ERROR, LOGTRACER_LOG_ID)
                << "DDSTracerEntities | ERROR: initResponseDataWriter() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}

void DDSTracerEntities::initRequestDataWriter() {
    DDS::DataWriterQos dataWriterQoS;
    ddsClient->getPublisher()->get_default_datawriter_qos(dataWriterQoS);

    dataWriterQoS.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

    DDS::DataWriter_var dataWriter = ddsClient->getPublisher()->create_datawriter(
            topicRequest, dataWriterQoS, nullptr, OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!dataWriter) {
        CLOG (ERROR, LOGTRACER_LOG_ID)
                << "DDSTracerEntities | ERROR: initRequestDataWriter() - OpenDDS Data Writer creation failed.";
        exit(EXIT_FAILURE);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    requestDataWriter = DDSLogTracerMessage::RequestDataWriter::_narrow(dataWriter);

    if (!requestDataWriter) {
        CLOG (ERROR, LOGTRACER_LOG_ID)
                << "DDSTracerEntities | ERROR: initRequestDataWriter() - OpenDDS Data Writer narrowing failed. ";
        exit(EXIT_FAILURE);
    }
}

void DDSTracerEntities::send(DDSLogTracerMessage::Response res) {
    CLOG (INFO, LOGTRACER_LOG_ID) << "DDSTracerEntities | sending response... ";

    DDS::ReturnCode_t error = responseDataWriter->write(res, DDS::HANDLE_NIL);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, LOGTRACER_LOG_ID) << "DDSTracerEntities | send() write returned " << error;

    }
}

void DDSTracerEntities::send(DDSLogTracerMessage::Request req) {
    CLOG (INFO, LOGTRACER_LOG_ID) << "DDSTracerEntities | sending request... ";

    DDS::ReturnCode_t error = requestDataWriter->write(req, DDS::HANDLE_NIL);

    if (error != DDS::RETCODE_OK) {
        CLOG (ERROR, LOGTRACER_LOG_ID) << "DDSTracerEntities | send() write returned " << error;

    }
}

void
DDSTracerEntities::waitUntilReadersConnected(int number) {
    DDS::StatusCondition_var condition = requestDataWriter->get_statuscondition();
    condition->set_enabled_statuses(DDS::SUBSCRIPTION_MATCHED_STATUS);

    DDS::WaitSet_var ws = new DDS::WaitSet;
    ws->attach_condition(condition);

    while (true) {
        DDS::PublicationMatchedStatus matches{};
        if (requestDataWriter->get_publication_matched_status(matches) != DDS::RETCODE_OK) {
            CLOG (ERROR, LOGTRACER_LOG_ID) << "DDSTracerEntities | subscription_matched_status failed!";
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


void DDSTracerEntities::addResponseCallback(std::function<void(DDSLogTracerMessage::Response)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<ReqResMessageListener *> (responseDataReader->get_listener());
    listener->addOnResponseCallback(std::move(callback));
}

void DDSTracerEntities::addRequestCallback(std::function<void(DDSLogTracerMessage::Request)> callback) {
    // downcast while inheritance is virtual
    auto *listener = dynamic_cast<ReqResMessageListener *> (requestDataReader->get_listener());
    listener->addOnRequestCallback(std::move(callback));
}

