/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include "Port.h"
#include "MessageProvider.h"
#include "tl/optional.hpp"
#include <set>

/**
 * A port that consists of multiple ports acting as one port in the architecture.
 * This can be used to utilize different communication technologies within the same port.
 * For example, a multiport with a CAN bus port and a websocket port could forward
 * its incoming messages to devices requiring different communication technologies.
 *
 * For each communication technology, add an appropriate port using addManagedPort(Port*).
 * By default, the multiport consists of a regular port.
 */
template <class T> class MultiPort : public Port<T>
{
protected:
  std::set<Port<T> *> managedPorts;

public:
  MultiPort () = default;

  void
  addManagedPort (Port<T> *portToAdd)
  {
    for (auto listener : this->observers)
      {
        portToAdd->attach (listener);
      }
    if (this->dataProvider != nullptr)
      {
        portToAdd->setDataProvidingPort (this->dataProvider);
      }
    managedPorts.emplace (portToAdd);
  }

  void
  removeManagedPort (Port<T> *portToAdd)
  {
    managedPorts.erase (portToAdd);
  }

  void
  attach (EventObserver *observer) override
  {
    Port<T>::attach (observer);
    for (auto port : managedPorts)
      {
        port->attach (observer);
      }
  }

  void
  detach (EventObserver *observer) override
  {
    this->detach (observer);
    for (auto port : managedPorts)
    {
      port->detach (observer);
    }
  }

  bool
  hasValue (sole::uuid requester) override
  {
    // A multiport has a value if any of its managed ports has a value
    for (auto port : managedPorts)
      {
        if (port->hasValue (requester))
          {
            return true;
          }
      }
    return false;
  }

  tl::optional<T>
  getCurrentValue (sole::uuid requester, bool mayRequestExternal = true) override
  {
    tl::optional<T> returnValue = tl::nullopt;
    for (auto port : managedPorts)
      {
        returnValue = port->getCurrentValue (requester, mayRequestExternal);
        if (returnValue != tl::nullopt)
          break;
      }
    return returnValue;
  }

  void
  getExternalMessages () override
  {
    for (auto port : managedPorts)
      {
        port->getExternalMessages ();
      }
  }

  void
  sendToExternal (tl::optional<T> nextVal) override
  {
    for (auto port : managedPorts)
      {
        port->sendToExternal (nextVal);
      }
  }

  void
  setDataProvidingPort (Port<T> *port) override
  {
    this->dataProvider = port;
    for (auto currentport : managedPorts)
      {
        currentport->setDataProvidingPort (port);
      }
  }

  void
  updateMessageSource () override
  {
    for (auto port : managedPorts)
      {
        port->updateMessageSource ();
      }
  }

  void
  setNextValue (T nextVal) override
  {
    for (auto port : managedPorts)
      {
        port->setNextValue (nextVal);
      }
  }

  void
  setNextValue (tl::optional<T> nextVal) override
  {
    for (auto port : managedPorts)
      {
        port->setNextValue (nextVal);
      }
  }
};