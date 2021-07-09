/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "ReqResMessageListener.h"


void
ReqResMessageListener::on_data_available(DDS::DataReader_ptr reader) {
    DDS::SampleInfo info{};
    DDS::ReturnCode_t error;

    if (strcmp(reader->get_topicdescription()->get_type_name(), REQ_MESSAGE_TYPE) == 0) {
        DDSLogTracerMessage::RequestDataReader_var reader_i
                = DDSLogTracerMessage::RequestDataReader::_narrow(reader);

        if (!reader_i) {
            CLOG (ERROR, DDS_LOGTRACER_LISTENER_LOG_ID) << "ReqResMessageListener | on_data_available _narrow DDSRecorderMessage failed!";
            exit(EXIT_FAILURE);
        }

        DDSLogTracerMessage::Request message;
        error = reader_i->take_next_sample(message, info);

        if (error == DDS::RETCODE_OK && info.valid_data) {
            on_data_available(message);
        }
    } else if (strcmp(reader->get_topicdescription()->get_type_name(), RES_MESSAGE_TYPE) == 0) {
        DDSLogTracerMessage::ResponseDataReader_var reader_i
                = DDSLogTracerMessage::ResponseDataReader::_narrow(reader);

        if (!reader_i) {
            CLOG (ERROR, DDS_LOGTRACER_LISTENER_LOG_ID) << "ReqResMessageListener | on_data_available _narrow DDSCommandMessage failed!";
            exit(EXIT_FAILURE);
        }

        DDSLogTracerMessage::Response message;
        error = reader_i->take_next_sample(message, info);

        if (error == DDS::RETCODE_OK && info.valid_data) {
            on_data_available(message);
        }
    }
}

void
ReqResMessageListener::addOnResponseCallback(
        std::function<void(DDSLogTracerMessage::Response)> callback) {
    onResponseCallback = std::move(callback);
}

void
ReqResMessageListener::addOnRequestCallback(
        std::function<void(DDSLogTracerMessage::Request)> callback) {
    onRequestCallback = std::move(callback);
}

void
ReqResMessageListener::on_data_available(const DDSLogTracerMessage::Request &message) {
    if (onRequestCallback) {
        onRequestCallback(message);
    }
}

void
ReqResMessageListener::on_data_available(const DDSLogTracerMessage::Response &message) {
    if (onResponseCallback) {
        onResponseCallback(message);
    }
}
/*
 * Unused methods
 */

void
ReqResMessageListener::on_requested_deadline_missed(
        DDS::DataReader_ptr /*reader*/, const DDS::RequestedDeadlineMissedStatus & /*status*/) {
}

void
ReqResMessageListener::on_requested_incompatible_qos(
        DDS::DataReader_ptr /*reader*/, const DDS::RequestedIncompatibleQosStatus & /*status*/) {
}

void
ReqResMessageListener::on_sample_rejected(DDS::DataReader_ptr /*reader*/,
                                    const DDS::SampleRejectedStatus & /*status*/) {
}

void
ReqResMessageListener::on_liveliness_changed(DDS::DataReader_ptr /*reader*/,
                                       const DDS::LivelinessChangedStatus & /*status*/) {
}

void
ReqResMessageListener::on_subscription_matched(DDS::DataReader_ptr reader,
                                         const DDS::SubscriptionMatchedStatus & status) {
    CLOG (DEBUG, DDS_LOGTRACER_LISTENER_LOG_ID) << "ReqResMessageListener | on_subscription_matched...";
}

void
ReqResMessageListener::on_sample_lost(DDS::DataReader_ptr /*reader*/,
                                const DDS::SampleLostStatus & /*status*/) {
}