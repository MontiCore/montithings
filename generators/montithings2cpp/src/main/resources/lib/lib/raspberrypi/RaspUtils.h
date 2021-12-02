/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "easyloggingpp/easylogging++.h"
#include <cstdint>

class RaspUtils
{
public:
  static void delay (long long int millis);
  static void delayMicroseconds (long long int microseconds);

  static uint16_t makeuint16 (uint8_t lsb, uint8_t msb);
};
