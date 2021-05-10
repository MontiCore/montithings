/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "EventObserver.h"
#include "UniqueElement.h"

enum TimeMode
{
  TIMESYNC,
  EVENTBASED
};

class IComponent : public EventObserver
{
  protected:
  std::string instanceName;

  public:
  virtual void setUp (TimeMode enclosingComponentTiming) = 0;
  virtual void init () = 0;
  virtual void compute () = 0;
  virtual void start () = 0;

  const std::string &getInstanceName () const
  {
    return instanceName;
  }

  void setInstanceName (const std::string &instanceName)
  {
    IComponent::instanceName = instanceName;
  }
};

