/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include "tl/optional.hpp"
#include "Port.h"
#include "MultiPort.h"

/**
 * The InOutPort consists of two ports (an incoming and an outgoing port)
 * that conceptually act as one port. This is useful if the communication
 * technology used for input does not match the communication technology
 * used for output.
 *
 * For example, imagine a composed component C forwards its incoming port P
 * to its subcomponent S. Futhermore, assume P gets its data from a websocket
 * while S can only be accessed using CAN bus. Using regular ports, this
 * situation cannot be implemented. By making P an InOutPort, the inport of P
 * can be a websocket port while the outgoing port can be a CAN bus based port.
 */
template<class T>
class InOutPort : public Port<T>
{
  protected:
  MultiPort<T> *inport = new MultiPort<T>;
  MultiPort<T> *outport = new MultiPort<T>;

  // a regular outport that is used to provide data to
  // components on the same system
  Port<T> *outportInterface;

  public:
  void registerListeningPort (sole::uuid requester) override
  {
    outportInterface->registerListeningPort (requester);
  }

  bool hasValue (sole::uuid requester) override
  {
    return outportInterface->hasValue (requester);
  }

  tl::optional<T> getCurrentValue (sole::uuid requester) override
  {
    return outportInterface->getCurrentValue (requester);
  }

  void getExternalMessages () override
  {
    // Only the inport is used for ingress. Hence, outport has nothing to get.
    inport->getExternalMessages ();
  }

  void sendToExternal (tl::optional<T> nextVal) override
  {
    // Only the outport is used for egress. Hence, inport has nothing to send.
    outport->sendToExternal (nextVal);
  }

  void setDataProvidingPort (Port<T> *port) override
  {
    // if a port provides data to the InOutPort, it should arrive at the inport
    inport->setDataProvidingPort (port);
  }

  void updateMessageSource () override
  {
    // intentionally left empty - inport and outport update themselves when needed
  }

  void setNextValue (T nextVal) override
  {
    inport->setNextValue (nextVal);
    // Immediately update outport as the InOutPort conceptually acts as one port.
    outport->updateMessageSource ();
  }

  void setNextValue (tl::optional<T> nextVal) override
  {
    inport->setNextValue (nextVal);
    // Immediately update outport as the InOutPort conceptually acts as one port.
    outport->updateMessageSource ();
  }

  InOutPort () {
    inport->addManagedPort(new Port<T> ());
    outportInterface = new Port<T> ();
    outport->addManagedPort(outportInterface);
    outport->setDataProvidingPort (inport);
  }

  InOutPort (MultiPort<T> *inport, MultiPort<T> *outport)
  {
    // throw away defaults
    delete this->inport;
    delete this->outport;

    this->inport = inport;
    this->outport = outport;
    inport->addManagedPort(new Port<T> ());
    outportInterface = new Port<T> ();
    outport->addManagedPort(outportInterface);
    outport->setDataProvidingPort (inport);
  }

  MultiPort<T> *getInport () const
  {
    return inport;
  }

  MultiPort<T> *getOutport () const
  {
    return outport;
  }
};
