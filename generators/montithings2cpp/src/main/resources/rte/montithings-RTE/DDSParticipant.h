#pragma once

#include <iostream>
#include <string>
#include <vector>

#include <dds/DdsDcpsInfrastructureC.h>
#include <dds/DdsDcpsPublicationC.h>

#include <dds/DCPS/Marked_Default_Qos.h>
#include <dds/DCPS/Service_Participant.h>
#include <dds/DCPS/WaitSet.h>

#include <dds/DCPS/StaticIncludes.h>

#include "dds/message-types/DDSMessageTypeSupportImpl.h"


class DDSParticipant {
protected:
    DDS::DomainParticipant_var participant;
    DDS::Publisher_var publisher;
    DDS::Subscriber_var subscriber;
    CORBA::String_var type_name;

public:
    virtual ~DDSParticipant() = default;
    DDS::DomainParticipant_var getParticipant() { return participant; }
    DDS::Publisher_var getPublisher() { return publisher; }
    DDS::Subscriber_var getSubscriber() { return subscriber; }
    CORBA::String_var getMessageTypeName(){ return type_name; }

    virtual std::string getInstanceName() = 0;
};
