/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "tl/optional.hpp"
#include "sole/sole.hpp"
#include "rigtorp/SPSCQueue.h"
#include <map>
#include "UniqueElement.h"
#include "string"

template<typename T>
class MessageProvider
{
  public:
  MessageProvider ()
  {
  };

  protected:
  typedef std::map<sole::uuid, rigtorp::SPSCQueue<T> > map_type;
  map_type queueMap;

  public:
  /* ============================================================ */
  /* ========================= Management ======================= */
  /* ============================================================ */

  /**
   * Registers an element that wants to receive messages from this message provider
   * \param requester uuid of the element that requests messages
   */
  virtual void registerListeningPort (sole::uuid requester)
  {
    this->queueMap[requester];
  }

  /**
   * Should get all available messages from whatever source this message
   * provider gets its input from
   */
  virtual void updateMessageSource () = 0;

  /* ============================================================ */
  /* ========================= Get Values ======================= */
  /* ============================================================ */

  /**
   * Checks if the provider has new messages for the element with the UUID "requester"
   * \param requester UUID of the element requesting a new message
   * \return true iff there is a message for the requesting element
   */
  virtual bool hasValue (sole::uuid requester)
  {
    if (this->queueMap[requester].front ())
      { return true; }
    updateMessageSource ();
    return this->queueMap[requester].front ();
  }

  /**
   * Queries the next message for a given UUID
   * \param requester the uuid of the element requesting a message
   * \return the next message for the requester, empty optional if no message present
   */
  virtual tl::optional<T> getCurrentValue (sole::uuid requester)
  {
    T queueElement;
    if (hasValue (requester))
      {
        queueElement = *(this->queueMap[requester].front ());
        this->queueMap[requester].pop ();
        tl::optional<T> currentValue = queueElement;
        return currentValue;
      }
    else
      {
        return tl::nullopt;
      }
  }
};

