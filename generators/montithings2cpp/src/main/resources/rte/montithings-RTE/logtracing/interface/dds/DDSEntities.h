/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include <string>

#include <dds/DCPS/Service_Participant.h>
#include <dds/DCPS/WaitSet.h>
#include <dds/DCPS/RTPS/RtpsDiscovery.h>

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

#include "ReqResMessageListener.h"

#include "message-types/DDSLogTracerMessageTypeSupportImpl.h"
#include <DDSClient.h>

#define LOGTRACER_LOG_ID "DDS"

class DDSEntities {

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

    DDSEntities() = default;

    ~DDSEntities() = default;

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
