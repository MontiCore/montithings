/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#include "RaspUtils.h"
#include <thread>

void
RaspUtils::delay (long long int millis)
{
  std::this_thread::sleep_for (std::chrono::milliseconds (millis));
}

void
RaspUtils::delayMicroseconds (long long int microseconds)
{
  std::this_thread::sleep_for (std::chrono::microseconds (microseconds));
}

uint16_t
RaspUtils::makeuint16 (uint8_t lsb, uint8_t msb)
{
  return ((msb & 0xFF) << 8) | (lsb & 0xFF);
}
