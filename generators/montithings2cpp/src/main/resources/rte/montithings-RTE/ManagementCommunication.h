/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include "UniqueElement.h"
#include "messages/Message.h"
#include "WSPort.h"
#include "ManagementMessageProcessor.h"
#include <iostream>
#include <set>

class ManagementCommunication : public UniqueElement
{
  protected:
  const char* configFilePath = "/app/bin/properties.json";

  std::future<void> futReceiveMessage;

  std::set<ManagementMessageProcessor*> messageProcessors;

  // IP of the device running this piece of code
  std::string ourIp;

  // IP of the IoT manager
  std::string managerIp;

  // Ports for management communication between composed comp and its subcomps
  WSPort<std::string>* managementIn  = nullptr;
  std::string managementInUri;

  public:
  // read IoT Manager IP from config file
  std::string readManagerIP();

  // read IoT Manager IP from config file
  std::string readComponentIP();

  // requests an IP from the manager for the component with the given instance name
  std::string getIpOfComponent(std::string componentInstanceName);

  void init(std::string managementPort);

  void receiveMessage();
  void registerMessageProcessor(ManagementMessageProcessor* processor);

  void sendManagementMessage(std::string receiverIp, std::string receiverPort, Message* message);
  void sendManagementMessage(std::string receiverIp, std::string receiverPort, std::string message);

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  const char *getConfigFilePath () const;
  const std::string &getOurIp () const;
  const std::string &getManagerIp () const;
};


