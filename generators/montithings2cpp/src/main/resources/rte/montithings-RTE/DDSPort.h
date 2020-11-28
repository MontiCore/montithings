/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include <ace/OS_NS_stdlib.h>
#include <future>
#include <iostream>

#include "DDSMessageTypeSupportC.h"
#include "DDSMessageTypeSupportImpl.h"
#include "DDSParticipant.h"
#include "Port.h"
#include "Utils.h"

template <typename T>
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

  // The DDS message type is keyed by a message id
  // After each message write the messageId is incremented so that each message can be identified uniquely  
  int messageId = 1;

  // Allow setting a callback function which is triggered whenever new data
  // arrives
  std::function<void(T)> onDataAvailableCallback;

public:
  explicit DDSPort(DDSParticipant &participant, Direction direction,
                   std::string topicName)
      : DDSPort(participant, direction, topicName, false) {}

  explicit DDSPort(DDSParticipant &participant, Direction direction,
                   std::string topicName, bool setQoSTransientDurability)
      : participant(&participant), direction(direction), topicName(topicName),
        setQoSTransientDurability(setQoSTransientDurability) {
    // independently of the port direction, a topic instance is required
    topic = createTopic();

    if (direction == INCOMING) {
      messageReader = initReader();
    } else {
      messageWriter = initWriter();
    }
  }

  ~DDSPort() {}

  void addOnDataAvailableCallbackHandler(std::function<void(T)> callback) {
    onDataAvailableCallback = callback;
  }

  DDS::Topic_var createTopic() {
    DDS::Topic_var topic = participant->getParticipant()->create_topic(
        // sets unique topic name which is associated with the publishers port
        // name
        topicName.c_str(),
        // Topics are type-specific
        participant->getMessageTypeName(),
        // QoS includes KEEP_LAST_HISTORY_QOS which might be changed
        // when log traces are inspected
        TOPIC_QOS_DEFAULT,
        // no topic listener required
        0,
        // default status mask ensures that
        // all relevant communication status
        // changes are communicated to the
        // application
        OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!topic) {
      std::cerr << "OpenDDS topic creation failed." << std::endl;
      exit(1);
    }

    return topic;
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
    // Default reliability is best effort. Thus, its changed to reliabe
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
      std::cerr << "OpenDDS data reader creation failed." << std::endl;
      exit(1);
    }

    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    DDSMessage::MessageDataReader_var messageReader =
        DDSMessage::MessageDataReader::_narrow(reader);

    if (!messageReader) {
      std::cerr << "OpenDDS message reader narrowing failed." << std::endl;
      exit(1);
    }
    return messageReader;
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
        0,
        // default status mask ensures that
        // all relevant communication status
        // changes are communicated to the
        // application
        OpenDDS::DCPS::DEFAULT_STATUS_MASK);

    if (!writer) {
      std::cerr << "OpenDDS Data Writer creation failed." << std::endl;
      exit(1);
    }

    // narrows the generic DataWriter to the type-specific DataWriter
    DDSMessage::MessageDataWriter_var messageWriter =
        DDSMessage::MessageDataWriter::_narrow(writer);

    if (!messageWriter) {
      std::cerr << "OpenDDS Data Writer narrowing failed. " << std::endl;
      exit(1);
    }

    return messageWriter;
  }

  void getExternalMessages() override {
    // Intentionally not implemented.
    // Functionality is provided by the listener callback functions.
  }

  void sendToExternal(tl::optional<T> nextVal) override {
    if (nextVal && direction == Direction::OUTGOING) {
      auto dataString = dataToJson(nextVal);

      DDSMessage::Message message;
      message.content_id = messageId;
      message.content = dataString.c_str();

      // Passing a DDS::HANDLE_NIL value indicates that the data writer should
      // determine the instance by inspecting the key of the sample.
      DDS::ReturnCode_t error = messageWriter->write(message, DDS::HANDLE_NIL);

      if (error != DDS::RETCODE_OK) {
        std::cerr << "write returned " << error << std::endl;
      }

      ++messageId;
    }
  }

  /*
   * DataReaderListener implementations
   */
  void on_data_available(DDS::DataReader_ptr reader) {
    // narrows the generic data reader passed into the listener to the
    // type-specific MessageDataReader interface
    DDSMessage::MessageDataReader_var reader_i =
        DDSMessage::MessageDataReader::_narrow(reader);

    if (!reader_i) {
      std::cerr << "ERROR: on_data_available() - _narrow failed!" << std::endl;
      return;
    }

    DDSMessage::Message message;
    DDS::SampleInfo info;

    DDS::ReturnCode_t error = reader_i->take_next_sample(message, info);

    if (error == DDS::RETCODE_OK && info.valid_data) {
      auto msg = message.content.in();
      T result = jsonToData<T>(msg);

      this->setNextValue(result);

      if (onDataAvailableCallback) {
        onDataAvailableCallback(result);
      }
    } else {
      std::cerr << "ERROR: on_data_available() - _ake_next_sample failed!" << std::endl;
      return;
    }
  }

  // Mandatory interface implementations which are left empty as we do not make use of them
  // Event triggers are logged nonetheless for potential inspection purposes

  void on_requested_deadline_missed(DDS::DataReader_ptr /*reader*/,
      const DDS::RequestedDeadlineMissedStatus & /*status*/) 
  {
    std::cout << "DDSPort::on_requested_deadline_missed" << std::endl;
  }

  void on_liveliness_changed(DDS::DataReader_ptr /*reader*/,
                             const DDS::LivelinessChangedStatus & /*status*/) 
  {
    std::cout << "DDSPort::on_liveliness_changed" << std::endl;
  }

  void on_requested_incompatible_qos(
      DDS::DataReader_ptr /*reader*/,
      const DDS::RequestedIncompatibleQosStatus & /*status*/) 
  {
    std::cout << "DDSPort::on_requested_incompatible_qos" << std::endl;
  }

  void on_sample_rejected(DDS::DataReader_ptr /*reader*/,
                          const DDS::SampleRejectedStatus & /*status*/)
  {
    std::cout << "DDSPort::on_sample_rejected" << std::endl;
  }

  void
  on_subscription_matched(DDS::DataReader_ptr /*reader*/,
                          const DDS::SubscriptionMatchedStatus & /*status*/) 
  {
    std::cout << "DDSPort::on_subscription_matched" << std::endl;
  }

  void on_sample_lost(DDS::DataReader_ptr /*reader*/,
                      const DDS::SampleLostStatus & /*status*/)
  {
    std::cout << "DDSPort::on_sample_lost" << std::endl;
  }
};
