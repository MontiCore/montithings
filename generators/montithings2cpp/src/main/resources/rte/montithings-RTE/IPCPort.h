/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "Port.h"
#include <iostream>
#include <nngpp/socket.h>
#include <nngpp/protocol/req0.h>
#include <nngpp/protocol/push0.h>
#include <cereal/archives/json.hpp>
#include <future>

template<typename T>
class IPCPort : public Port<T>
{
  private:
  nng::socket socket;
  const char *uri;
  bool isConnected = false;
  Direction direction;
  std::future<bool> fut;

  public:
  IPCPort (Direction direction, const char *uri) : direction (direction), uri (uri)
  {
    this->uri = uri;
    //Open Socket in Request mode
    if (direction == IN)
      { socket = nng::req::open (); }
    else
      { socket = nng::push::open (); }
    //Dial specifies, that it connects to an already established socket (the server)
    try
      {
        socket.dial (uri, nng::flag::alloc);
      }
    catch (const std::exception &e)
      {
        std::cout << "Connection to " << uri << " could not be established! (" << e.what () << ")\n";
        return;
      }
    std::cout << "Connection to " << uri << " established\n";
    if (direction == OUT)
      {
        fut = std::async (std::launch::async, &IPCPort::run, this);
      }
  }

  public:
  void getExternalMessages () override
  {
    if (!isConnected)
      {
        try
          {
            socket.dial (uri, nng::flag::alloc);
          }
        catch (const std::exception &e)
          {
            std::cout << "Connection to " << uri << " could not be established! (" << e.what () << ")\n";
            return;
          }
        isConnected = true;
      }
    //Sending an empty request to initialize data transfer from the ipc port.
    socket.send ("");

    //Receive Message and convert to target type T
    auto msg = socket.recv_msg ();
    auto data = msg.body ().template data<char> ();
    std::string receivedAnswer (msg.body ().template data<char> ());
    std::stringstream inStream (receivedAnswer);
    cereal::JSONInputArchive inputArchive (inStream);
    T result;
    inputArchive (result);

    this->setNextValue (result);
  }

  bool run ()
  {
    while (true)
      {
        tl::optional<T> dataOpt = this->dataProvider->getCurrentValue (this->uuid);

        if (dataOpt)
          {
            if (!isConnected)
              {
                try
                  {
                    socket.dial (uri, nng::flag::alloc);
                  }
                catch (const std::exception &e)
                  {
                    std::cout << "Connection to " << uri << " could not be established! (" << e.what () << ")\n";
                    continue;
                  }
              }
            isConnected = true;
            T data = dataOpt.value ();
            std::ostringstream stream;
            {
              cereal::JSONOutputArchive outputArchive (stream);
              outputArchive (data);
            }
            auto dataString = stream.str ();
            dataString = stream.str ();
            socket.send (nng::buffer (nng_strdup (dataString.c_str ()), dataString.length () + 1), nng::flag::alloc);

            std::cout << dataString << "\n";
          }
        else
          {
            std::this_thread::yield ();
            std::this_thread::sleep_for (std::chrono::milliseconds (50));
          }
      }
  }
};
