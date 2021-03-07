/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include <csignal>
#include <cstdlib>
#include <iostream>

#include "../montithings-RTE/easyloggingpp/easylogging++.h"

#include "lib/cxxopts.hpp"
#include "lib/loguru.hpp"

#include "MessageFlowRecorder.h"

INITIALIZE_EASYLOGGINGPP

// Initializing recorder as a global variable, as it is used in the signal handler
MessageFlowRecorder recorder;

void
signalHandler (int s)
{
  LOG_F (INFO, "Caught signal %d, stopping..", s);
  recorder.stop ();
  recorder.process ();
  recorder.cleanup ();
  recorder.saveToFile ();
  exit (EXIT_SUCCESS);
}

int
main (int argc, char **argv)
{
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
  loguru::init (argc, argv);

  // enables writing logs to file
  loguru::add_file ("everything.log", loguru::Append, loguru::Verbosity_MAX);
  loguru::add_file ("latest_readable.log", loguru::Truncate, loguru::Verbosity_INFO);

  // registers signal handler in order to intercept a ctrl-c event
  signal (SIGINT, signalHandler);

  cxxopts::Options options ("MessageFlowRecorder", "A brief description");
  options.add_options () ("i,inforepo", "DCPSInfoRepo host",
                          cxxopts::value<std::string> ()->default_value ("localhost:12345")) (
      "file-recordings", "File name where recordings are saved",
      cxxopts::value<std::string> ()->default_value ("recordings.json")) (
      "n",
      "Amount of outgoing ports of the application. When defined, the recorder will wait until all "
      "of these ports are connected to the recorder. Otherwise the recording will start after at "
      "least one port connected.",
      cxxopts::value<int> ()->default_value ("1")) (
      "v,verbose", "Verbose output -v n {OFF,FATAL,ERROR,WARNING,INFO,0-9}",
      cxxopts::value<bool> ()->default_value ("false")) ("h,help", "Print usage");

  auto result = options.parse (argc, argv);

  if (result.count ("help"))
    {
      std::cout << options.help () << std::endl;
      exit (0);
    }

  if (result.count ("inforepo") == 0)
    {
      LOG_F (ERROR, "Please specify the DCPSInfoRepo host (-i, -inforepo).");
      exit (0);
    }

  LOG_F (INFO, "Initializing...");

  std::string fileRecordingsPath = result["file-recordings"].as<std::string> ();
  std::string dcpsInfoHost = result["inforepo"].as<std::string> ();
  int appInstancesAmount = result["n"].as<int> ();
  bool verbose = result["verbose"].as<bool> ();
  LOG_F (INFO, "Storing Records in %s", fileRecordingsPath.c_str ());

  recorder.setDcpsInfoRepoHost (dcpsInfoHost);
  recorder.setInstanceAmount (appInstancesAmount);
  recorder.init ();
  recorder.setFileRecordings (fileRecordingsPath);
  recorder.setVerbose (verbose);

  // std::this_thread::sleep_for (std::chrono::seconds (3));

  recorder.start ();
}