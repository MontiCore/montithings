/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "EventObserver.h"
#include <set>

class EventSource
{
protected:
  /// observers that should be triggered when an event happens
  std::set<EventObserver *> observers;

public:
  /**
   * triggers the onEvent() callback of the observers
   */
  virtual void
  notify ()
  {
    for (EventObserver *observer : observers)
    {
      if (observer != nullptr)
      {
        observer->onEvent ();
      }
    }
  }

  /**
   * Add a new observer to notify when an event happens
   * \param observer the observer to be added
   */
  virtual void
  attach (EventObserver *observer)
  {
    if (observer != nullptr)
    {
      observers.emplace (observer);
    }
  }

  /**
   * Remove an observer from the set of observers to be notified when an event
   * happens
   * \param observer the observer to be removed
   */
  virtual void
  detach (EventObserver *observer)
  {
    if (observer != nullptr)
    {
      observers.erase (observer);
    }
  }
};