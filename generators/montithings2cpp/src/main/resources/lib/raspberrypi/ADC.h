/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include "Pi2c.h"
#include <cinttypes>

class ADC
{
protected:
  std::string i2cDevice = "/dev/i2c-1";
  Pi2c i2c;
  uint8_t address = 0x04;

public:
  explicit ADC (uint8_t address = 0x04);
  int readRaw (uint8_t channel);
  int readVoltage (uint8_t channel);
  int read (uint8_t channel);
  uint16_t readRegister (uint8_t channel);
  uint16_t version ();
};
