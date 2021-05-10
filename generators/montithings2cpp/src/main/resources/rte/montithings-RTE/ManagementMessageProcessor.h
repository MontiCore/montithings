/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "messages/Message.h"

class ManagementMessageProcessor {
  public:
  /**
   * Process one message, i.e.
   */
  virtual void process(std::string msg) = 0;
};
