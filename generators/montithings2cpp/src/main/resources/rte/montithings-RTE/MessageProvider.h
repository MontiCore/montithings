/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "tl/optional.hpp"
#include "sole/sole.hpp"
#include "rigtorp/SPSCQueue.h"
#include <map>
#include <mutex>
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
  typedef std::map<sole::uuid, std::pair<rigtorp::SPSCQueue<T>, std::mutex> > map_type;
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
    this->queueMap[requester].second.lock ();
    if (this->queueMap[requester].first.front ())
      {
        this->queueMap[requester].second.unlock ();
        return true;
      }
    this->queueMap[requester].second.unlock ();
    updateMessageSource ();
    this->queueMap[requester].second.lock ();
    bool result = this->queueMap[requester].first.front ();
    this->queueMap[requester].second.unlock ();
    return result;
  }

  /**
   * Queries the next message for a given UUID
   * \param requester the uuid of the element requesting a message
   * \return the next message for the requester, empty optional if no message present
   */
  virtual tl::optional<T> getCurrentValue (sole::uuid requester)
  {
    updateMessageSource ();

    this->queueMap[requester].second.lock ();
    T *frontElement = this->queueMap[requester].first.front ();
    if (frontElement == nullptr)
      {
        // no element available :(
        this->queueMap[requester].second.unlock ();
        return tl::nullopt;
      }

    // we found something :)
    tl::optional<T> currentValue = *(frontElement);

    // remove the element we just pulled from the queue
    this->queueMap[requester].first.pop ();
    this->queueMap[requester].second.unlock ();

    return currentValue;
  }
};

