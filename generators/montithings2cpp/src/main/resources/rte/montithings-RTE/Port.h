// (c) https://github.com/MontiCore/monticore
#pragma once
#include "tl/optional.hpp"
#include "MessageAcceptor.h"
#include "MessageProvider.h"

enum Direction
{
  IN, OUT
};

template<class T>
class Port : public MessageAcceptor<T>, public MessageProvider<T>, public UniqueElement
{
  protected:
  /// The port that this port gets its incoming data from
  Port<T> *dataProvider = nullptr;

  /// Indicates whether an connection has already been established
  bool connectionEstablished = false;

  public:
  /**
   * This method is to be implemented by all ports that get messages from
   * external sources, e.g. through web sockets or CAN bus
   */
  virtual void getExternalMessages ()
  {
  }

  /**
   * Send the given message to external message acceptors, e.g. using web sockets or CAN bus
   * \param nextVal the value to be sent. May be empty
   */
  virtual void sendToExternal(tl::optional<T> nextVal)
  {
  }

  virtual void setDataProvidingPort (Port<T> *port)
  {
    dataProvider = port;
    dataProvider->registerListeningPort (this->uuid);
  }

  void updateMessageSource () override
  {
    if (this->dataProvider == nullptr)
      {
        getExternalMessages ();
        return;
      }
    if (this->dataProvider->hasValue (this->uuid))
      {
        this->setNextValue (this->dataProvider->getCurrentValue (this->uuid));
      }
  }

  void setNextValue (T nextVal) override
  {
    if (!this->queueMap.size ())
      {
        this->sendToExternal(nextVal);
      }
    for (auto &x : this->queueMap)
      {
        x.second.second.lock ();
        x.second.first.push (nextVal);
        x.second.second.unlock ();
      }
  }

  void setNextValue (tl::optional<T> nextVal) override
  {
    if (nextVal)
      {
        setNextValue (nextVal.value ());
      }
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  Port ()
  {
  }

  bool isConnectionEstablished () const
  {
    return connectionEstablished;
  }
};
