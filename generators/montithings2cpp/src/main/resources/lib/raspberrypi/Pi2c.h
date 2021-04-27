/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "easyloggingpp/easylogging++.h"
#include <cstring>
#include <fcntl.h>
#include <linux/i2c-dev.h>
#include <mutex>
#include <stdio.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <time.h>
#include <unistd.h>

class Pi2c
{
public:
  void init (std::string devPath, int devAddr);

  void send (uint8_t reg);
  void send (uint8_t cmd, uint8_t *data, uint8_t len);
  void receive (uint8_t cmd, uint8_t *data, uint8_t len);
  uint8_t receiveByte (uint8_t cmd);

protected:
  int fd;
  std::mutex channelMutex;
};