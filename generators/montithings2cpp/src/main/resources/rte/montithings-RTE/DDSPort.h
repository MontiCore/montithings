/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include <ace/OS_NS_stdlib.h>
#include <future>
#include <iostream>
#include <utility>
#include "easyloggingpp/easylogging++.h"

#include "dds/message-types/DDSMessageTypeSupportC.h"
#include "dds/message-types/DDSMessageTypeSupportImpl.h"
#include "dds/recorder/DDSRecorder.h"
#include "dds/recorder/MessageWithClockContainer.h"
#include "dds/recorder/VectorClock.h"

#include "DDSParticipant.h"
#include "Port.h"
#include "Utils.h"

#define DDS_LOG_ID "DDS"

template<typename T>
class DDSPort
        : public Port<T>,
          public virtual OpenDDS::DCPS::LocalObject<DDS::DataReaderListener> {
private:
    std::string topicName;
    Direction direction;

    // DDS specific variables
    DDSParticipant *participant;
    DDS::Topic_var topic;
    DDSMessage::MessageDataWriter_var messageWriter;
    DDSMessage::MessageDataReader_var messageReader;
    bool setQoSTransientDurability;
    bool isRecordingEnabled;

    // The DDS message type is keyed by a message id
    // After each message write the messageId is incremented so that each message can be identified uniquely
    int messageId = 1;

    // Allow setting a callback function which is triggered whenever new data
    // arrives
    std::function<void(T)> onDataAvailableCallback;

    // Using pointer to avoid initializing it despite not being enabled in pom.xml
    std::unique_ptr<DDSRecorder> ddsRecorder;

public:
    explicit DDSPort(DDSParticipant &participant, Direction direction, std::string  topicName,
                      bool isRecordingEnabled, bool setQoSTransientDurability, std::function<void(T)> onDataAvailableCallback)
            : onDataAvailableCallback(onDataAvailableCallback),
              participant(&participant),
              direction(direction),
              topicName(std::move(topicName)),
              isRecordingEnabled(isRecordingEnabled),
              setQoSTransientDurability(setQoSTransientDurability) {
        init();
    }

    explicit DDSPort(DDSParticipant &participant, Direction direction, std::string topicName,
                      bool isRecordingEnabled, bool setQoSTransientDurability)
            : participant(&participant),
              direction(direction),
              topicName(std::move(topicName)),
              isRecordingEnabled(isRecordingEnabled),
              setQoSTransientDurability(setQoSTransientDurability) {
        init();
    }

    void init() {

    	if (isRecordingEnabled) {
            ddsRecorder = std::make_unique<DDSRecorder>();
            ddsRecorder->setInstanceName(participant->getInstanceName());
            ddsRecorder->setTopicName(topicName);
            ddsRecorder->init();
        }
        // independently of the port direction, a topic instance is required
        topic = createTopic();

        if (!topic) {
            CLOG (ERROR, DDS_LOG_ID) << "ERROR: DDSPort() - OpenDDS topic creation failed.";
        } else {
            if (direction == INCOMING) {
                messageReader = initReader();
            } else {
                messageWriter = initWriter();
            }
        }
    }

    ~DDSPort() override = default;


    DDS::Topic_var createTopic() {
        DDS::Topic_var topicVar = participant->getParticipant()->create_topic(
                // sets unique topicVar name which is associated with the publishers port
                // name
                topicName.c_str(),
                // Topics are type-specific
                participant->getMessageTypeName(),
                // QoS includes KEEP_LAST_HISTORY_QOS which might be changed
                // when log traces are inspected
                TOPIC_QOS_DEFAULT,
                // no topicVar listener required
                nullptr,
                // default status mask ensures that
                // all relevant communication status
                // changes are communicated to the
                // application
                OpenDDS::DCPS::DEFAULT_STATUS_MASK);
        return topicVar;
    }

    DDSMessage::MessageDataReader_var initReader() {
        // Registers the own instance as a listener,
        // thus the derived methods of the DataReaderListener are implemented down
        // below
        DDS::DataReaderListener_var listener(this);

        // Definitions of the QoS settings
        DDS::DataReaderQos dataReaderQos;

        // Applies default qos settings
        participant->getSubscriber()->get_default_datareader_qos(dataReaderQos);
        // Default reliability is best effort. Thus, its changed to reliable
        // communication
        dataReaderQos.reliability.kind = DDS::RELIABLE_RELIABILITY_QOS;

        if (setQoSTransientDurability) {
            dataReaderQos.history.kind = DDS::KEEP_ALL_HISTORY_QOS;
            dataReaderQos.resource_limits.max_samples_per_instance =
                    DDS::LENGTH_UNLIMITED;
            dataReaderQos.durability.kind = DDS::TRANSIENT_DURABILITY_QOS;
        }

        DDS::DataReader_var reader =
                participant->getSubscriber()->create_datareader(
                        topic, dataReaderQos, listener,
                        // default status mask ensures that
                        // all relevant communication status
                        // changes are communicated to the
                        // application
                        OpenDDS::DCPS::DEFAULT_STATUS_MASK);

        if (!reader) {
            CLOG (ERROR, DDS_LOG_ID) << "ERROR: initReader() - OpenDDS data reader creation failed.";
            return 0;
        }

        // narrows the generic data reader passed into the listener to the
        // type-specific MessageDataReader interface
        DDSMessage::MessageDataReader_var messageReaderVar =
                DDSMessage::MessageDataReader::_narrow(reader);

        if (!messageReaderVar) {
            CLOG (ERROR, DDS_LOG_ID) << "ERROR: initReader() - OpenDDS message reader narrowing failed.";
        }
        return messageReaderVar;
    }

    DDSMessage::MessageDataWriter_var initWriter() {
        DDS::DataWriterQos dataWriterQoS;
        participant->getPublisher()->get_default_datawriter_qos(dataWriterQoS);

        if (setQoSTransientDurability) {
            dataWriterQoS.history.kind = DDS::KEEP_ALL_HISTORY_QOS;
            dataWriterQoS.resource_limits.max_samples_per_instance =
                    DDS::LENGTH_UNLIMITED;
            dataWriterQoS.durability.kind = DDS::TRANSIENT_DURABILITY_QOS;
        }

        DDS::DataWriter_var writer = participant->getPublisher()->create_datawriter(
                topic, dataWriterQoS,
                // no listener required
                nullptr,
                // default status mask ensures that
                // all relevant communication status
                // changes are communicated to the
                // application
                OpenDDS::DCPS::DEFAULT_STATUS_MASK);

        if (!writer) {
            CLOG (ERROR, DDS_LOG_ID) << "ERROR: initWriter() - OpenDDS Data Writer creation failed.";
            exit (EXIT_FAILURE);
        }

        // narrows the generic DataWriter to the type-specific DataWriter
        DDSMessage::MessageDataWriter_var messageWriterVar =
                DDSMessage::MessageDataWriter::_narrow(writer);

        if (!messageWriterVar) {
            CLOG (ERROR, DDS_LOG_ID) << "ERROR: initWriter() - OpenDDS Data Writer narrowing failed. ";
        }

        return messageWriterVar;
    }

    void getExternalMessages() override {
        // Intentionally not implemented.
        // Functionality is provided by the listener callback functions.
    }

    void sendToExternal(tl::optional<T> nextVal) override {
        if (nextVal && direction == Direction::OUTGOING) {
            if (!messageWriter) {
                CLOG (ERROR, DDS_LOG_ID) << "ERROR: sendToExternal() - writer not initialized ";
                return;
            }

            DDSMessage::Message message;
            message.id = messageId;

            if (isRecordingEnabled) {
                MessageWithClockContainer <T> container;
                container.message = nextVal.value();
                container.vectorClock = VectorClock::getVectorClock();
                auto dataString = dataToJson(container);
                message.content = dataString.c_str();
            } else {
                auto dataString = dataToJson(nextVal);
                message.content = dataString.c_str();
            }

            // Passing a DDS::HANDLE_NIL value indicates that the data writer should
            // determine the instance by inspecting the key of the sample.
            DDS::ReturnCode_t error = messageWriter->write(message, DDS::HANDLE_NIL);

            if (error != DDS::RETCODE_OK) {
                CLOG (ERROR, DDS_LOG_ID) << "ERROR: sendToExternal() - write returned " << error;
            }

            if (isRecordingEnabled) {
                // remove vector clock
                message.content = dataToJson(nextVal).c_str();
                ddsRecorder->recordMessage(message, messageWriter->get_topic()->get_name(),
                                           VectorClock::getVectorClock(), false);
            }

            ++messageId;
        }
    }

    /*
     * DataReaderListener implementations
     */
    void on_data_available(DDS::DataReader_ptr reader) override {
        // narrows the generic data reader passed into the listener to the
        // type-specific MessageDataReader interface
        DDSMessage::MessageDataReader_var reader_i =
                DDSMessage::MessageDataReader::_narrow(reader);

        if (!reader_i) {
            CLOG (ERROR, DDS_LOG_ID) << "ERROR: on_data_available() - _narrow failed!";
            return;
        }

        DDSMessage::Message message;
        DDS::SampleInfo info{};

        DDS::ReturnCode_t error = reader_i->take_next_sample(message, info);

        if (error == DDS::RETCODE_OK && info.valid_data) {
            auto msg = message.content.in();

            T result;
            if (isRecordingEnabled) {
                MessageWithClockContainer <T> container;
                container = jsonToData<MessageWithClockContainer < T> > (msg);

                const char *topicId = reader_i->get_topicdescription()->get_name();

                // remove vector clock
                message.content = dataToJson(container.message).c_str();

                ddsRecorder->recordMessage(message, topicId, container.vectorClock, false);

                result = container.message;
            } else {
                result = jsonToData<T>(msg);
            }

            this->setNextValue(result);

            if (onDataAvailableCallback) {
                onDataAvailableCallback(result);
            }
        } else {
            CLOG (ERROR, DDS_LOG_ID) << "on_data_available() - _take_next_sample failed!";
            return;
        }
    }

    // Mandatory interface implementations which are left empty as we do not make use of them
    // Event triggers are logged nonetheless for potential inspection purposes

    void on_requested_deadline_missed(DDS::DataReader_ptr /*reader*/,
                                      const DDS::RequestedDeadlineMissedStatus & /*status*/) override {
        CLOG (DEBUG, DDS_LOG_ID) << "DDSPort::on_requested_deadline_missed";
    }

    void on_liveliness_changed(DDS::DataReader_ptr /*reader*/,
                               const DDS::LivelinessChangedStatus & /*status*/) override {
        CLOG (DEBUG, DDS_LOG_ID) << "DDSPort::on_liveliness_changed";
    }

    void on_requested_incompatible_qos(
            DDS::DataReader_ptr /*reader*/,
            const DDS::RequestedIncompatibleQosStatus & /*status*/) override {
        CLOG (DEBUG, DDS_LOG_ID) << "DDSPort::on_requested_incompatible_qos";
    }

    void on_sample_rejected(DDS::DataReader_ptr /*reader*/,
                            const DDS::SampleRejectedStatus & /*status*/) override {
        CLOG (DEBUG, DDS_LOG_ID) << "DDSPort::on_sample_rejected";
    }

    void
    on_subscription_matched(DDS::DataReader_ptr /*reader*/,
                            const DDS::SubscriptionMatchedStatus & /*status*/) override {
        CLOG (DEBUG, DDS_LOG_ID) << "DDSPort::on_subscription_matched";
    }

    void on_sample_lost(DDS::DataReader_ptr /*reader*/,
                        const DDS::SampleLostStatus & /*status*/) override {
        CLOG (DEBUG, DDS_LOG_ID) << "DDSPort::on_sample_lost";
    }
};
