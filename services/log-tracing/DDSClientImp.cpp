/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include "DDSClientImpl.h"


DDSClientImpl::DDSClientImpl(int argc, char *argv[]) {
    while (!this->tryInitializeDDS(argc, argv)) {
        CLOG (DEBUG, "DDS") << "Creating dds instances failed. Is multicast enabled/allowed?";
        CLOG (DEBUG, "DDS") << "Trying again...";
        std::this_thread::sleep_for(std::chrono::seconds(1));
    }
}

bool
DDSClientImpl::tryInitializeDDS(int argc, char *argv[]) {
    DDS::DomainParticipantFactory_var dpf = TheParticipantFactoryWithArgs(argc, argv);

    // We do not make use of multiple DDS domains yet, arbitrary but fixed id will do it
    int domainId = 42;
    if (!participant) {
        participant = dpf->create_participant(domainId, PARTICIPANT_QOS_DEFAULT,
                // no listener required
                                              0,
                // default status mask ensures that
                // all relevant communication status
                // changes are communicated to the
                // application
                                              OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    }

    if (!participant) {
        std::cerr << "DDS creation of the participant instance failed." << std::endl;
        return false;
    }

    DDSMessage::MessageTypeSupport_var ts = new DDSMessage::MessageTypeSupportImpl;

    if (ts->register_type(participant, "") != DDS::RETCODE_OK) {
        std::cerr << "DDS creation of the message type support failed." << std::endl;
        return false;
    }

    if (!publisher) {
        publisher = participant->create_publisher(PUBLISHER_QOS_DEFAULT, 0,
                                                  OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    }
    if (!publisher) {
        std::cerr << "DDS creation of the publisher instance failed." << std::endl;
        return false;
    }
    if (!subscriber) {
        subscriber = participant->create_subscriber(SUBSCRIBER_QOS_DEFAULT, 0,
                                                    OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    }
    if (!subscriber) {
        std::cerr << "DDS creation of the subscriber instance failed." << std::endl;
        return false;
    }

    return true;
}
