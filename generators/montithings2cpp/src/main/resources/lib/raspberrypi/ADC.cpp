/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "ADC.h"
#include "RaspUtils.h"

ADC::ADC (uint8_t address)
{
  this->address = address;
  i2c.init (i2cDevice, address);
}

int
ADC::readRaw (uint8_t channel)
{
  uint8_t addr = 0x10 + channel;
  return readRegister (addr);
}

int
ADC::readVoltage (uint8_t channel)
{
  uint8_t addr = 0x20 + channel;
  return readRegister (addr);
}

int
ADC::read (uint8_t channel)
{
  uint8_t addr = 0x30 + channel;
  return readRegister (addr);
}

uint16_t
ADC::readRegister (uint8_t channel)
{
  uint8_t buffer[2];
  i2c.receive (channel, buffer, 2);
  return RaspUtils::makeuint16 (buffer[0], buffer[1]);
}

uint16_t
ADC::version ()
{
  return readRegister (0x2);
}
