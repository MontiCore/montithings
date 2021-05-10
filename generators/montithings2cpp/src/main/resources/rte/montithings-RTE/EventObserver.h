/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "UniqueElement.h"

class EventObserver : public UniqueElement
{
public:
  /// callback triggered when an event happens
  virtual void onEvent () = 0;
};