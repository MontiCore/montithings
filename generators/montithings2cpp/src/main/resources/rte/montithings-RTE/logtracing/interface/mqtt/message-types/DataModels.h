/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include <string>
#include "sole/sole.hpp"

enum RequestType {
    LOG_ENTRIES,
    INTERNAL_DATA,
    TRACE_DATA
};

struct RequestStruct {
    sole::uuid req_uuid;
    sole::uuid log_uuid;
    sole::uuid input_uuid;
    sole::uuid output_uuid;
    std::string instance_name;

    long from_timestamp;
    RequestType req_type;

    template <class Archive>
    void serialize( Archive & ar )
    {
        ar( req_uuid, log_uuid, input_uuid, output_uuid, instance_name, from_timestamp, req_type );
    }
};

struct ResponseStruct {
    sole::uuid req_uuid;
    RequestType req_type;
    std::string content;

    template <class Archive>
    void serialize( Archive & ar )
    {
        ar( req_uuid, req_type, content );
    }
};