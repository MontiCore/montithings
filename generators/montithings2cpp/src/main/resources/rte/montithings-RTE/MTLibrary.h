/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include <string>
#include <chrono>
#include <thread>

#include "cereal/archives/json.hpp"
#include "easyloggingpp/easylogging++.h"

#include "Utils.h"


namespace montithings
{
namespace library
{

void
delay (int milliseconds);

long
now();

void
log (const std::string& message);

// nano timestamps cannot be stored in type long
// unfortunately MC does not support types like "unsigned long long"
std::string
now_ns();
}
}