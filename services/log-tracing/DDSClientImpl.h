// (c) https://github.com/MontiCore/monticore



#pragma once
#include <ace/Log_Msg.h>
#include <iostream>
#include <string>
#include <vector>
#include <chrono>
#include <thread>
#include "easyloggingpp/easylogging++.h"

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsPublicationC.h>
#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/Service_Participant.h>
#include <dds/DCPS/StaticIncludes.h>

#include <dds/DCPS/RTPS/RtpsDiscovery.h>
#include <dds/DCPS/transport/rtps_udp/RtpsUdp.h>

#include "DDSPort.h"
#include "DDSClient.h"

class DDSClientImpl : public DDSClient
{
    private:
        bool tryInitializeDDS(int argc, char *argv[]);

    public:
        DDSClientImpl (int argc, char *argv[]);
        ~DDSClientImpl() = default;

        json getSerializedState() {
            // the middleware is not a component and thus, no state is present
            return json::object();
        }


        std::string getInstanceName() {
            return "logtracer_middleware";
        }
};

