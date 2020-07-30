/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */
#pragma once
#include <set>
#include "tl/optional.hpp"
#include "Port.h"

template<class T>
class MultiPort : public Port<T>
{
  protected:
  std::set<Port<T> *> managedPorts;
  std::set<sole::uuid> listeningPorts;
  Port<T> *dataProvidingPort = nullptr;

  public:
  MultiPort ()
  {
    managedPorts.emplace (new Port<T> ());
  }

  void addManagedPort (Port<T> *portToAdd)
  {
    for (auto listener : listeningPorts)
      {
        portToAdd->registerListeningPort (listener);
      }
    if (dataProvidingPort != nullptr)
      {
        portToAdd->setDataProvidingPort (dataProvidingPort);
      }
    managedPorts.emplace (portToAdd);
  }

  void removeManagedPort (Port<T> *portToAdd)
  {
    managedPorts.erase(portToAdd);
  }

  void registerListeningPort (sole::uuid requester) override
  {
    listeningPorts.emplace (requester);
    for (auto port : managedPorts)
      {
        port->registerListeningPort (requester);
      }
  }

  bool hasValue (sole::uuid requester) override
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

  tl::optional<T> getCurrentValue (sole::uuid requester) override
  {
    tl::optional<T> returnValue = tl::nullopt;
    for (auto port : managedPorts)
      {
        returnValue = port->getCurrentValue (requester);
        if (returnValue != tl::nullopt) break;
      }
    return returnValue;
  }

  void getExternalMessages () override
  {
    for (auto port : managedPorts)
      {
        port->getExternalMessages ();
      }
  }

  void sendToExternal (tl::optional<T> nextVal) override
  {
    for (auto port : managedPorts)
      {
        port->sendToExternal (nextVal);
      }
  }

  void setDataProvidingPort (Port<T> *port) override
  {
    dataProvidingPort = port;
    for (auto currentport : managedPorts)
      {
        currentport->setDataProvidingPort (port);
      }
  }

  void updateMessageSource () override
  {
    for (auto port : managedPorts)
      {
        port->updateMessageSource ();
      }
  }

  void setNextValue (T nextVal) override
  {
    for (auto port : managedPorts)
      {
        port->setNextValue (nextVal);
      }
  }

  void setNextValue (tl::optional<T> nextVal) override
  {
    for (auto port : managedPorts)
      {
        port->setNextValue (nextVal);
      }
  }
};