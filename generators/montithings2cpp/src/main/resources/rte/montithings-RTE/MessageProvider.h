/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "EventObserver.h"
#include "EventSource.h"
#include "UniqueElement.h"
#include "rigtorp/SPSCQueue.h"
#include "sole/sole.hpp"
#include "string"
#include "tl/optional.hpp"
#include <map>
#include <mutex>

template <typename T> class MessageProvider : public virtual EventSource
{
public:
  MessageProvider (){};

protected:
  typedef std::map<sole::uuid, std::pair<rigtorp::SPSCQueue<T>, std::mutex>> map_type;
  map_type queueMap;

public:
  /* ============================================================ */
  /* ========================= Management ======================= */
  /* ============================================================ */

  void
  attach (EventObserver *observer) override
  {
    EventSource::attach (observer);
    this->queueMap[observer->getUuid ()];
  }

  void
  detach (EventObserver *observer) override
  {
    EventSource::detach (observer);
    this->queueMap.erase (observer->getUuid ());
  }

  /**
   * Should get all available messages from whatever source this message
   * provider gets its input from
   */
  virtual void updateMessageSource () = 0;

  /**
   * This method is to be implemented by all providers that get messages from
   * external sources, e.g. through web sockets or CAN bus
   */
  virtual void
  getExternalMessages ()
  {
    // Intentionally left empty.
    // Internal components do not have external message sources
  }

  /* ============================================================ */
  /* ========================= Get Values ======================= */
  /* ============================================================ */

  /**
   * Checks if the provider has new messages for the element with the UUID "requester"
   * \param requester UUID of the element requesting a new message
   * \return true iff there is a message for the requesting element
   */
  virtual bool
  hasValue (sole::uuid requester)
  {
    this->queueMap[requester].second.lock ();
    if (this->queueMap[requester].first.front ())
      {
        this->queueMap[requester].second.unlock ();
        return true;
      }
    this->queueMap[requester].second.unlock ();
    return false;
  }

  /**
   * Queries the next message for a given UUID
   * \param requester the uuid of the element requesting a message
   * \return the next message for the requester, empty optional if no message present
   */
  virtual tl::optional<T>
  getCurrentValue (sole::uuid requester, bool mayRequestExternal = true)
  {
    if (!MessageProvider<T>::hasValue (requester) && mayRequestExternal)
      {
        updateMessageSource ();
      }

    // pull new message if queue is empty
    if (!MessageProvider<T>::hasValue (requester) && mayRequestExternal)
      {
        getExternalMessages ();
      }

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
