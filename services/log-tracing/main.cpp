/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#include <cstdlib>
#include <iostream>

#include "easyloggingpp/easylogging++.h"
#include "logtracing/interface/LogTracerInterface.h"
#include "logtracing/interface/dds/LogTracerDDSClient.h"
#include "logtracing/interface/mqtt/LogTracerMQTTClient.h"
#include "logtracing/data/InternalDataResponse.h"
#include "Utils.h"


#include "lib/cxxopts.hpp"
#include "lib/loguru.hpp"
#include "lib/crow_all.h"
#include "lib/json.hpp"

INITIALIZE_EASYLOGGINGPP

// forward declaration, is implemented down below
void onResponse(sole::uuid reqUuid, std::string content);

LogTracerInterface *interface;
std::map<sole::uuid, std::string> responses;

int
main(int argc, char **argv) {
    crow::SimpleApp app;

    CROW_ROUTE(app, "/logs/<string>")
            ([](std::string instanceName) {
                crow::response res;
                res.add_header("Access-Control-Allow-Origin", "*");
                res.add_header("Content-Type", "application/json");
                sole::uuid reqUuid = interface->request(instanceName, LogTracerInterface::Request::LOG_ENTRIES,
                                                        time(0));

                std::chrono::steady_clock::time_point begin = std::chrono::steady_clock::now();
                std::chrono::steady_clock::time_point current = std::chrono::steady_clock::now();
                while (responses[reqUuid].empty() &&
                       std::chrono::duration_cast<std::chrono::seconds>(current - begin).count() < 2) {
                    std::this_thread::sleep_for(std::chrono::milliseconds(10));
                    std::this_thread::yield();
                    current = std::chrono::steady_clock::now();
                }

                if (responses[reqUuid].empty()) {
                    return crow::response(404);
                }

                std::string response = responses[reqUuid];
                responses.erase(reqUuid);

                auto jContent = nlohmann::json::parse(response);

                nlohmann::json jEntryFormatted;
                nlohmann::json jEntries;
                for(auto& entry : jContent["value0"]) {
                    jEntryFormatted["log_uuid"] = entry["value0"];
                    jEntryFormatted["index"] = entry["value1"];
                    jEntryFormatted["index_second"] = entry["value2"];
                    jEntryFormatted["time"] = entry["value3"];
                    jEntryFormatted["message"] = entry["value4"];
                    jEntryFormatted["input_uuid"] = entry["value5"];
                    jEntryFormatted["output_uuid"] = entry["value6"];

                    jEntries.push_back(jEntryFormatted);
                }
                res.write(jEntries.dump());

                return res;
            });

    CROW_ROUTE(app, "/logs/<string>/<string>/<string>/<string>")
            ([](std::string instanceName, std::string logUuidStr,
                std::string inputUuidStr, std::string outputUuidStr) {
                crow::response res;
                res.add_header("Access-Control-Allow-Origin", "*");
                res.add_header("Content-Type", "application/json");
                sole::uuid logUuid = sole::rebuild(logUuidStr);
                sole::uuid inputUuid = sole::rebuild(inputUuidStr);
                sole::uuid outputUuid = sole::rebuild(outputUuidStr);

                sole::uuid reqUuid = interface->request(instanceName,
                                                        LogTracerInterface::Request::INTERNAL_DATA,
                                                        time(0),
                                                        logUuid, inputUuid, outputUuid);

                std::chrono::steady_clock::time_point begin = std::chrono::steady_clock::now();
                std::chrono::steady_clock::time_point current = std::chrono::steady_clock::now();
                while (responses[reqUuid].empty() &&
                        std::chrono::duration_cast<std::chrono::seconds>(current - begin).count() < 2) {
                    std::this_thread::sleep_for(std::chrono::milliseconds(10));
                    std::this_thread::yield();
                    std::cout << std::chrono::duration_cast<std::chrono::microseconds>(current - begin).count() << std::endl;
                    current = std::chrono::steady_clock::now();
                }

                if (responses[reqUuid].empty()) {
                    return crow::response(404);
                }

                std::string response = responses[reqUuid];
                responses.erase(reqUuid);


                montithings::InternalDataResponse internalDataResponse = jsonToData<montithings::InternalDataResponse>(response);

                nlohmann::json jRes;
                jRes["sources_ports_map"] = dataToJson(internalDataResponse.getSourcesOfPortsMap());
                jRes["var_snapshot"] = dataToJson(internalDataResponse.getVarSnapshot());
                jRes["inputs"] = internalDataResponse.getInput();
                jRes["external_ports"] = internalDataResponse.getExternalPorts();
                jRes["traces"] = dataToJson(internalDataResponse.getTracesUuidsWithPortNames());
                jRes["traces_decomposed"] = dataToJson(internalDataResponse.getTracesUuidsWithPortNamesDecomposed());

                res.write(jRes.dump());

                return res;
            });

    CROW_ROUTE(app, "/trace/<string>/<string>")
            ([](std::string instanceName, std::string traceUuidStr) {
                crow::response res;
                res.add_header("Access-Control-Allow-Origin", "*");
                res.add_header("Content-Type", "application/json");
                sole::uuid traceUuid = sole::rebuild(traceUuidStr);

                sole::uuid reqUuid = interface->request(instanceName,
                                                        LogTracerInterface::Request::TRACE_DATA,
                                                        traceUuid);
                std::chrono::steady_clock::time_point begin = std::chrono::steady_clock::now();
                std::chrono::steady_clock::time_point current = std::chrono::steady_clock::now();
                while (responses[reqUuid].empty() &&
                        std::chrono::duration_cast<std::chrono::seconds>(current - begin).count() < 2) {
                    std::this_thread::sleep_for(std::chrono::milliseconds(10));
                    std::this_thread::yield();
                    current = std::chrono::steady_clock::now();
                }

                if (responses[reqUuid].empty()) {
                    return crow::response(404);
                }

                std::string response = responses[reqUuid];
                responses.erase(reqUuid);

                montithings::InternalDataResponse internalDataResponse = jsonToData<montithings::InternalDataResponse>(response);

                nlohmann::json jRes;
                jRes["sources_ports_map"] = dataToJson(internalDataResponse.getSourcesOfPortsMap());
                jRes["var_snapshot"] = dataToJson(internalDataResponse.getVarSnapshot());
                jRes["inputs"] = internalDataResponse.getInput();
                jRes["external_ports"] = internalDataResponse.getExternalPorts();
                jRes["traces"] = dataToJson(internalDataResponse.getTracesUuidsWithPortNames());
                jRes["traces_decomposed"] = dataToJson(internalDataResponse.getTracesUuidsWithPortNamesDecomposed());

                res.write(jRes.dump());

                return res;
            });

    el::Loggers::getLogger("DDS");
    el::Loggers::getLogger("MQTT");

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


    if (messageBroker == "DDS") {
        interface = new LogTracerDDSClient(argc, argv,
                                           "middleware",
                                           true,
                                           false,
                                           false,
                                           true);
    } else if (messageBroker == "MQTT") {
        std::string instanceName = "middleware";
        interface = new LogTracerMQTTClient(instanceName, true);
    } else {
        std::cerr << "Message broker " << messageBroker << " is not supported!" << std::endl;
        exit(EXIT_FAILURE);
    }

    interface->addOnResponseCallback(std::bind(&onResponse, std::placeholders::_1, std::placeholders::_2));

    LOG_F (INFO, "Wait for connections...");
    interface->waitUntilReadersConnected(1);

    app.port(8080).multithreaded().run();
}

void onResponse(sole::uuid reqUuid, std::string content) {
    LOG_F (INFO, "Got response ...");
    responses[reqUuid] = content;
}