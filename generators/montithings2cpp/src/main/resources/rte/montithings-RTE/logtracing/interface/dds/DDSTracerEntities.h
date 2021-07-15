/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

/**
 * Class dedicated to initialize and manage specific DDS entities which are used for communication related to log tracing.
 * In order to reuse the DDS participant, subscriber, and publisher, the DDSClient instance is passed and used.
 */

#pragma once

#include <string>

#include <dds/DCPS/WaitSet.h>
#include <dds/DCPS/RTPS/RtpsDiscovery.h>

#include "ReqResMessageListener.h"
#include "message-types/DDSLogTracerMessageTypeSupportImpl.h"
#include <DDSClient.h>

#define LOGTRACER_LOG_ID "DDS_LOGTRACER"

class DDSTracerEntities {

private:
    const char *REQ_MESSAGE_TYPE = "Request Type";
    const char *RES_MESSAGE_TYPE = "Response Type";
    const char *REQ_TOPIC = "Requests";
    const char *RES_TOPIC = "Response";

    std::string instanceName;

    DDSLogTracerMessage::RequestTypeSupport_var requestTypeSupport;
    DDSLogTracerMessage::ResponseTypeSupport_var responseTypeSupport;
    DDSLogTracerMessage::RequestDataReader_var requestDataReader;
    DDSLogTracerMessage::ResponseDataReader_var responseDataReader;
    DDSLogTracerMessage::RequestDataWriter_var requestDataWriter;
    DDSLogTracerMessage::ResponseDataWriter_var responseDataWriter;

    DDS::Topic_var topicResponse;
    DDS::Topic_var topicRequest;
    DDS::ContentFilteredTopic_var topicRequestFiltered;

protected:
    DDSClient *ddsClient;

    DDSTracerEntities() = default;

    ~DDSTracerEntities() = default;

    void setInstanceName(std::string name);

    void initMessageType();

    void initTopic();

    void initRequestDataReader();

    void initResponseDataReader();

    void initRequestDataWriter();

    void initResponseDataWriter();

    void waitUntilReadersConnected(int number);

    void send(DDSLogTracerMessage::Request req);

    void send(DDSLogTracerMessage::Response res);

    void addResponseCallback(std::function<void(DDSLogTracerMessage::Response)> callback);

    void addRequestCallback(std::function<void(DDSLogTracerMessage::Request)> callback);

public:

};
