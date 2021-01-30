// (c) https://github.com/MontiCore/monticore
#pragma once
#include "MessageAcceptor.h"
#include "MessageProvider.h"
#include "tl/optional.hpp"

enum Direction
{
  INCOMING,
  OUTGOING
};

template <class T>
class Port : public MessageAcceptor<T>, public MessageProvider<T>, public EventObserver
{
protected:
  /// The port that this port gets its incoming data from
  Port<T> *dataProvider = nullptr;

  /// Indicates whether an connection has already been established
  bool connectionEstablished = false;

  /// Ports that need to be notified when new data is available
  std::set<Port<T> *> portsToNotify;

  /// Contains the UUID of a requester that should not be notified to avoid loops
  tl::optional<sole::uuid> suppressNotification = tl::nullopt;

public:
  /**
   * Send the given message to external message acceptors, e.g. using web sockets or CAN bus
   * \param nextVal the value to be sent. May be empty
   */
  virtual void
  sendToExternal (tl::optional<T> nextVal)
  {
    // Intentionally left empty.
    // Internal components do not send messages to external services
  }

  virtual void
  setDataProvidingPort (Port<T> *port)
  {
    dataProvider = port;
    dataProvider->attach (this);
  }

  bool
  hasValue (sole::uuid requester) override
  {
    bool messageInQueue = MessageProvider<T>::hasValue (requester);
    if (messageInQueue)
      {
        return true;
      }
    else if (this->dataProvider != nullptr)
      {
        // check if our message source has something for us
        return this->dataProvider->hasValue (this->uuid);
      }
    return false;
  }

  void
  updateMessageSource () override
  {
    if (this->dataProvider == nullptr)
      {
        return;
      }
    if (this->dataProvider->hasValue (this->uuid))
      {
        this->setNextValue (this->dataProvider->getCurrentValue (this->uuid));
      }
  }

  void
  setNextValue (T nextVal) override
  {
    if (!this->queueMap.size ())
      {
        this->sendToExternal (nextVal);
      }
    for (auto &x : this->queueMap)
      {
        x.second.second.lock ();
        while (!x.second.first.try_push (nextVal))
          {
            // if no more messages can be added, remove the oldest message
            // to make room for new messages
            x.second.first.pop ();
          }
        x.second.second.unlock ();
      }

    // notify event-based elements about new input
    this->notify (suppressNotification);
  }

  tl::optional<T>
  getCurrentValue (sole::uuid requester) override
  {
    // getCurrentValue's updateMessageSource() call can cause new messages
    // to be added to the queue. As we're already querying the queue here,
    // we need to suppress additional events to prevent endless loops
    suppressNotification = requester;
    tl::optional<T> result = MessageProvider<T>::getCurrentValue (requester);
    suppressNotification = tl::nullopt;
    return result;
  }

  void
  setNextValue (tl::optional<T> nextVal) override
  {
    if (nextVal)
      {
        setNextValue (nextVal.value ());
      }
  }

  void
  onEvent () override
  {
    updateMessageSource ();
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  Port () {}

  virtual ~Port () = default;

  bool
  isConnectionEstablished () const
  {
    return connectionEstablished;
  }
};
