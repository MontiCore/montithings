/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include <csignal>
#include <cstdlib>
#include <iostream>
#include <future>

#include "../montithings-RTE/easyloggingpp/easylogging++.h"

#include "lib/cxxopts.hpp"
#include "lib/loguru.hpp"

#include "MessageFlowRecorder.h"

INITIALIZE_EASYLOGGINGPP

// Initializing recorder as a global variable, as it is used in the signal handler
MessageFlowRecorder recorder;

void
signalHandler(int s) {
    LOG_F (INFO, "Caught signal %d, stopping..", s);
    recorder.stop();
}

int
main(int argc, char **argv) {
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

    // registers signal handler in order to intercept a ctrl-c event
    signal(SIGINT, signalHandler);

    cxxopts::Options options("MessageFlowRecorder", "A brief description");
    options.add_options()
            ("DCPSConfigFile", "DCPSConfigFile", cxxopts::value<std::string>()->default_value("dcpsconfig.ini"))
            ("DCPSInfoRepo", "DCPSInfoRepo host", cxxopts::value<std::string>()->default_value(""))
            ("stopAfter", "Stop recording after given minutes", cxxopts::value<int>())
            ("minSpacing", "Minimum spacing in ms between each message sent to the same component.",
             cxxopts::value<int>()->default_value("0"))
            ("fileRecordings", "File name where recordings are saved",
             cxxopts::value<std::string>()->default_value("recordings.json"))
            ("n",
             "Number of outgoing ports of the application. When defined, the recorder will wait until all of these ports are connected to the recorder. "
             "Otherwise the recording will start after at least one port connected.",
             cxxopts::value<int>()->default_value("1"))
            ("h,help", "Print usage");

    auto result = options.parse(argc, argv);

    if (result.count("help")) {
        std::cout << options.help() << std::endl;
        exit(EXIT_FAILURE);
    }

    LOG_F (INFO, "Initializing...");
    std::string fileRecordingsPath = result["fileRecordings"].as<std::string>();
    std::string dcpsConfigFile = result["DCPSConfigFile"].as<std::string>();
    std::string dcpsInfoHost = result["DCPSInfoRepo"].as<std::string>();
    int minSpacing = result["minSpacing"].as<int>();

    if (dcpsInfoHost.empty()) {
        std::cout << "Please provide the following argument: -DCPSInfoRepo" << std::endl;
        exit(EXIT_FAILURE);
    }

    if (dcpsConfigFile.empty()) {
        std::cout << "Please provide the following argument: -dcpsConfigFile" << std::endl;
        exit(EXIT_FAILURE);
    }

    std::future<void> future;
    if (result.count("stopAfter")) {
        int stopAfter = result["stopAfter"].as<int>();

        future = std::async(std::launch::async, [&]() {
            LOG_F (INFO, "Stopping recording in %d minutes", stopAfter);

            std::chrono::minutes dura(stopAfter);
            std::this_thread::sleep_for(dura);

            LOG_F (INFO, "%d minutes elapsed, stopping recording...", stopAfter);
            recorder.stop();
        });
    }

    int appInstancesNumber = result["n"].as<int>();
    LOG_F (INFO, "Storing Records in %s", fileRecordingsPath.c_str());

    recorder.setDcpsInfoRepoHost(dcpsInfoHost);
    recorder.setInstanceNumber(appInstancesNumber);
    recorder.setMinSpacing(minSpacing);

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

    recorder.init(argc, argv);
    recorder.setFileRecordings(fileRecordingsPath);

    recorder.start();
}