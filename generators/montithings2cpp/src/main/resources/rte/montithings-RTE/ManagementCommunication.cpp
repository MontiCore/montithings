/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "ManagementCommunication.h"
#include "json/json.hpp"
#include "cpp-httplib/httplib.h"
#include <fstream>
#include <utility>

using json = nlohmann::json;

// read IoT Manager IP from config file
std::string
ManagementCommunication::readManagerIP ()
{
  std::ifstream i (this->configFilePath);
  std::string manager = "";
  // TODO: Is a fallback ip necessary/ does it make sense?
  if (i.fail ())
    {
      manager = "127.0.0.1";
    }
  else
    {
      json j;
      i >> j;
      manager = j["managerIp"].get<std::string> ();
    }
  return manager;
}

// get own IP from IoT Manager
std::string
ManagementCommunication::readComponentIP ()
{
  std::ifstream i (this->configFilePath);
  std::string ip = "";
  // TODO: Is a fallback ip necessary/ does it make sense?
  if (i.fail ())
    {
      ip = "127.0.0.1";
    }
  else
    {
      json j;
      i >> j;
      ip = j["componentIp"].get<std::string> ();
    }
  return ip;
}

std::string
ManagementCommunication::getIpOfComponent (std::string componentInstanceName)
{
  httplib::Client cli (managerIp, 8080);

  std::string body;

  //TODO: add application name to route
  auto res = cli.Get ("/api/v1/resources/distribution/latest?component=source",
                      [&] (const char *data, size_t data_length)
                      {
                        body.append (data, data_length);
                        return true;
                      });

  //TODO: Also get port from api
  std::string ip = "";
  if (!res || (res && res->status != 200))
    {
      ip = "127.0.0.1";
    }
  else
    {
      ip = body;
    }
  return ip;
}

void
ManagementCommunication::init (std::string managementPort)
{
  // read IoT Manager IP from config file
  managerIp = readManagerIP ();

  // read own IP from config file
  ourIp = readComponentIP ();

  // communication in_port
  managementInUri = "ws://" + ourIp + ":" + managementPort;
  managementIn = new WSPort<std::string> (INCOMING, managementInUri, false);

  futReceiveMessage = std::async (std::launch::async, &ManagementCommunication::receiveMessage, this);
}

void
ManagementCommunication::receiveMessage ()
{
  while (true)
    {
      std::cout << "Waiting for connection\n";

      tl::optional<std::string> msg = managementIn->getCurrentValue (uuid);
      if (msg)
        {
          for (auto processor : messageProcessors)
            {
              processor->process (msg.value ());
            }
        }
      else
        { std::this_thread::sleep_for (std::chrono::milliseconds (1000)); }
    }
}

void
ManagementCommunication::registerMessageProcessor (ManagementMessageProcessor *processor)
{
  messageProcessors.emplace (processor);
}

void
ManagementCommunication::sendManagementMessage (std::string receiverIp, std::string receiverPort, Message *message)
{
  sendManagementMessage (receiverIp, receiverPort, message->toString ());
}

void
ManagementCommunication::sendManagementMessage (std::string receiverIp, std::string receiverPort, std::string message)
{
  // tell subcomponent where to connect to
  std::string uri = "ws://" + receiverIp + ":" + receiverPort;
  WSPort<std::string> *managementOut = new WSPort<std::string> (OUTGOING, uri, false);
  std::this_thread::sleep_for (std::chrono::milliseconds (1000));
  managementOut->setNextValue (message);

  // kill communication port
  std::this_thread::sleep_for (std::chrono::milliseconds (1000));
  managementOut->killThread ();
  delete managementOut;
}

/* ============================================================ */
/* ======================= GENERATED CODE ===================== */
/* ============================================================ */

const char *
ManagementCommunication::getConfigFilePath () const
{
  return configFilePath;
}

const std::string &
ManagementCommunication::getOurIp () const
{
  return ourIp;
}

const std::string &
ManagementCommunication::getManagerIp () const
{
  return managerIp;
}
