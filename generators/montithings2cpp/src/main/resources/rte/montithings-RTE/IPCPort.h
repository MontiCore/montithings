/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include "Port.h"
#include <iostream>
#include <nngpp/socket.h>
#include <nngpp/protocol/req0.h>
#include <nngpp/protocol/push0.h>
#include "Utils.h"
#include "easyloggingpp/easylogging++.h"

template<typename T>
class IPCPort : public Port<T>
{
  private:
  nng::socket socket;
  const char *uri;
  bool isConnected = false;
  Direction direction;

  protected:
  /**
   * Tries to open a connection to an IPC port
   * \return true iff connection was successfully opened; false otherwise
   */
  bool openConnection ()
  {
    try
      {
        //Dial specifies, that it connects to an already established socket (the server)
        socket.dial (uri, nng::flag::alloc);
      }
    catch (const std::exception &e)
      {
        LOG(ERROR) << "Connection to " << uri << " could not be established! (" << e.what () << ")";
        return false;
      }
    LOG(DEBUG) << "Connection to " << uri << " established";
    return true;
  }

  public:
  IPCPort (Direction direction, const char *uri) : direction (direction), uri (uri)
  {
    this->uri = uri;
    if (direction == INCOMING)
      //Open Socket in Request mode
      { socket = nng::req::open (); }
    else
      { socket = nng::push::open (); }
    isConnected = openConnection ();
  }

  public:
  void getExternalMessages () override
  {
    if (!isConnected)
      {
        isConnected = openConnection ();
        if (isConnected)
          { return; }
      }
    //Sending an empty request to initialize data transfer from the ipc port.
    socket.send ("");

    //Receive Message and convert to target type T
    auto msg = socket.recv_msg ();
    auto data = msg.body ().template data<char> ();
    T result = jsonToData<T> (data);
    this->setNextValue (result);
  }

  void sendToExternal (tl::optional<T> nextVal) override
  {
    if (nextVal)
      {
        if (!isConnected)
          {
            isConnected = openConnection ();
            if (isConnected)
              { return; }
          }
        auto dataString = dataToJson (nextVal);
        socket.send (nng::buffer (nng_strdup (dataString.c_str ()), dataString.length () + 1), nng::flag::alloc);

        LOG(DEBUG) << dataString;
      }
  }
};