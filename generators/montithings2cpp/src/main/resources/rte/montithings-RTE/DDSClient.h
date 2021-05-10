// (c) https://github.com/MontiCore/monticore
#pragma once

#include <iostream>
#include <string>
#include <vector>
#include <chrono>
#include <thread>

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsPublicationC.h>
#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/Service_Participant.h>
#include <dds/DCPS/StaticIncludes.h>

#include "easyloggingpp/easylogging++.h"
#include "json/json.hpp"

#include "dds/message-types/DDSMessageTypeSupportImpl.h"

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

using json = nlohmann::json;

class DDSClient {
protected:
    DDS::DomainParticipant_var participant;
    DDS::Publisher_var publisher;
    DDS::Subscriber_var subscriber;
    CORBA::String_var type_name;

public:
    DDS::DomainParticipant_var getParticipant() { return participant; }

    DDS::Publisher_var getPublisher() { return publisher; }

    DDS::Subscriber_var getSubscriber() { return subscriber; }

    CORBA::String_var getMessageTypeName() { return type_name; }

    virtual std::string getInstanceName() = 0;

    virtual json getSerializedState() = 0;

};

