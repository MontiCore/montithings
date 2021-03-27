/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once

#include <unordered_map>

#include "../../cereal/types/unordered_map.hpp"

template <typename T> struct MessageWithClockContainer
{
  using clock = std::unordered_map<std::string, long>;

  T message;
  clock vectorClock;

  template <class Archive>
  void
  serialize (Archive &archive)
  {
    archive (message, vectorClock);
  }
};
