/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include <cstdlib>
#include <iostream>

#include "easyloggingpp/easylogging++.h"
#include "logtracing/interface/LogTracerInterface.h"
#include "logtracing/interface/dds/LogTracerDDSClient.h"

#include "lib/cxxopts.hpp"
#include "lib/loguru.hpp"

INITIALIZE_EASYLOGGINGPP

// forward declaration, is implemented down below
void onResponse(sole::uuid reqUuid, std::string content);

int
main(int argc, char **argv) {
    el::Loggers::getLogger("DDS");

    // only show most relevant things on stderr:
    loguru::g_internal_verbosity = false;
    loguru::g_stderr_verbosity = 0;
    loguru::g_colorlogtostderr = true;
    loguru::g_preamble_header = false;
    loguru::g_preamble = true;
    loguru::g_preamble_date = false;
    loguru::g_preamble_time = true;
    loguru::g_preamble_uptime = true;
    loguru::g_preamble_thread = false;
    loguru::g_preamble_file = false;
    loguru::g_preamble_verbose = false;
    loguru::g_preamble_pipe = true;

    // but allow overwriting through argument -v
    loguru::init(argc, argv);

    // enables writing logs to file
    loguru::add_file("everything.log", loguru::Append, loguru::Verbosity_MAX);
    loguru::add_file("latest_readable.log", loguru::Truncate, loguru::Verbosity_INFO);


    cxxopts::Options options("LogTracer", "A brief description");
    options.add_options()
            ("message-broker", "The used MessageBroker (MQTT or DDS). Defaults to DDS.",
             cxxopts::value<std::string>()->default_value("DDS"))
            ("DCPSConfigFile", "DCPSConfigFile", cxxopts::value<std::string>()->default_value("dcpsconfig.ini"))
            ("DCPSInfoRepo", "DCPSInfoRepo host", cxxopts::value<std::string>()->default_value(""))
            ("h,help", "Print usage");

    auto result = options.parse(argc, argv);

    if (result.count("help")) {
        std::cout << options.help() << std::endl;
        exit(EXIT_SUCCESS);
    }

    LOG_F (INFO, "Initializing...");
    std::string messageBroker = result["message-broker"].as<std::string>();

    std::string dcpsConfigFile = result["DCPSConfigFile"].as<std::string>();
    std::string dcpsInfoHost = result["DCPSInfoRepo"].as<std::string>();


    // Rename named arguments since cxxopts does not allow a single "-" in front of multiple characters
    // Unfortunately this is what OpenDDS expects
    for (int i = 1; i < argc; ++i) {
        std::string arg = argv[i];
        if (!arg.compare("--DCPSInfoRepo")) {
            argv[i] = strdup("-DCPSInfoRepo");
        } else if (!arg.compare("--DCPSConfigFile")) {
            argv[i] = strdup("-DCPSConfigFile");
        }
    }

    LogTracerInterface *interface;
    if (messageBroker == "DDS") {
        interface = new LogTracerDDSClient(argc, argv,
                                           true,
                                           false,
                                           false,
                                           true);
    } else if (messageBroker == "MQTT") {

    } else {
        std::cerr << "Message broker " << messageBroker << " is not supported!" << std::endl;
        exit(EXIT_FAILURE);
    }

    std::string instanceName = "hierarchy.Example.source";
    time_t fromDatetime = time(nullptr);

    interface->addOnResponseCallback(std::bind(&onResponse, std::placeholders::_1, std::placeholders::_2));

    interface->waitUntilReadersConnected(1);

    std::this_thread::sleep_for(std::chrono::seconds(1));

    interface->request(instanceName, LogTracerInterface::Request::LOG_ENTRIES, fromDatetime);
    interface->request(instanceName, LogTracerInterface::Request::INTERNAL_DATA, fromDatetime);

    std::this_thread::sleep_for(std::chrono::seconds(3));

    interface->cleanup();
}

void onResponse(sole::uuid reqUuid, std::string content) {
    std::cout << content;
}