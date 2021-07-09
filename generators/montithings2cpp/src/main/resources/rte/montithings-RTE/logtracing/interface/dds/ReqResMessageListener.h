/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

/**
 * Listener Implementation called by lower level DDS instances whenever new data is received.
 */

#pragma once
#include <iostream>
#include <functional>
#include <string>
#include <vector>
#include <utility>
#include <future>

#include "../../../easyloggingpp/easylogging++.h"

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsSubscriptionC.h>
#include <dds/DCPS/LocalObject.h>
#include <dds/DCPS/Definitions.h>

#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/StaticIncludes.h>

#include "message-types/DDSLogTracerMessageTypeSupportImpl.h"

#define DDS_LOGTRACER_LISTENER_LOG_ID "DDS_LOGTRACER_LISTENER"

class ReqResMessageListener
      : public virtual OpenDDS::DCPS::LocalObject<DDS::DataReaderListener>
{
private:
    const char *REQ_MESSAGE_TYPE = "Request Type";
    const char *RES_MESSAGE_TYPE = "Response Type";

    std::function<void(DDSLogTracerMessage::Response)> onResponseCallback;
    std::function<void(DDSLogTracerMessage::Request)> onRequestCallback;

    void on_requested_deadline_missed(DDS::DataReader_ptr reader, const DDS::RequestedDeadlineMissedStatus &status) override;
    void on_requested_incompatible_qos(DDS::DataReader_ptr reader, const DDS::RequestedIncompatibleQosStatus &status) override;
    void on_sample_rejected(DDS::DataReader_ptr reader, const DDS::SampleRejectedStatus &status) override;
    void on_liveliness_changed(DDS::DataReader_ptr reader, const DDS::LivelinessChangedStatus &status) override;
    void on_subscription_matched(DDS::DataReader_ptr reader, const DDS::SubscriptionMatchedStatus &status) override;
    void on_sample_lost(DDS::DataReader_ptr reader, const DDS::SampleLostStatus &status) override;
    void on_data_available(DDS::DataReader_ptr reader) override;
    void on_data_available(const DDSLogTracerMessage::Request& message);
    void on_data_available(const DDSLogTracerMessage::Response& message);

public:
    ReqResMessageListener() = default;
    ~ReqResMessageListener() = default;

    void addOnResponseCallback (std::function<void(DDSLogTracerMessage::Response)> callback);
    void addOnRequestCallback (std::function<void(DDSLogTracerMessage::Request)> callback);
};