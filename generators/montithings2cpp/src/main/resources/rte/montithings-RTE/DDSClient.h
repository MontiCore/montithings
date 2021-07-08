// (c) https://github.com/MontiCore/monticore

/**
 * The current integration of OpenDDS is not very flexible.
 * Unlike MQTT, subscriptions and publications are done by instantiating multiple OpenDDS classes.
 * As this depends on the component type, corresponding classes (<component>DDSClient) are generated which implement this abstract class.
 * 
 * Instantiated DDSClients are given to port instances and, if enabled, to the recording module.
 * Thus, multiple DDS specific instances are avoided.
 */

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

// Supporting all transport types with a statically built OpenDDS
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
    
    // TODO
    CORBA::String_var type_name;

public:
    DDS::DomainParticipant_var getParticipant() { return participant; }

    DDS::Publisher_var getPublisher() { return publisher; }

    DDS::Subscriber_var getSubscriber() { return subscriber; }

    CORBA::String_var getMessageTypeName() { return type_name; }


    // The DDSClient is passed to several instances.
    // E.g. The port instance makes use of it, but also instantiates and maintains 
    // the recording module, which in turn, makes use of the DDSClient as well.
    // Unfortunately the port instance has no knowledge about the component instance name.
    // This however, is required by the recording module which is maintained by the port.
    // Hence the support for retrieving the instance name from the DDSClient
    virtual std::string getInstanceName() = 0;

    // TODO
    virtual json getSerializedState() = 0;
};

