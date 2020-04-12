/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include <nngpp/socket.h>
#include <nngpp/protocol/sub0.h>
#include <nngpp/protocol/pub0.h>
#include <future>
#include <iostream>
#include <cereal/archives/json.hpp>
#include "Port.h"



template<typename T>
class WSPort : public Port<T>
{
  private:
  nng::socket outSocket;
  std::future<bool> futOutSocket;
  nng::socket inSocket;
  std::future<bool> futInSocket;
  const char *uri;
  Direction direction;

  public:
  explicit WSPort (Direction direction, const char *uri) : uri (uri), direction (direction)
  {
    if (direction == IN)
      {
        //Open Socket in Request mode
        inSocket = nng::sub::open ();
        nng_setopt (inSocket.get (), NNG_OPT_SUB_SUBSCRIBE, "", 0);
        //Dial specifies, that it connects to an already established socket (the server)
        try
          {
            inSocket.listen (uri, nng::flag::alloc);
          }
        catch (const std::exception &e)
          {
            std::cout << "Could not create listener for: " << uri << " (" << e.what () << ")\n";
            return;
          }
        std::cout << "Created listener for: " << uri << "\n";
        futInSocket = std::async (std::launch::async, &WSPort::listen, this);
      }
    else
      {
        //Open Socket in publish mode
        outSocket = nng::pub::open ();
        nng::set_opt_reconnect_time_max (outSocket, 2000);
        nng::set_opt_reconnect_time_min (outSocket, 100);
        futOutSocket = std::async (std::launch::async, &WSPort::send, this);
      }
  }

  bool listen ()
  {
    //Receive Message and convert to target type T
    while (true)
      {
        auto msg = inSocket.recv_msg ();
        auto data = msg.body ().template data<char> ();
        std::string receivedAnswer (msg.body ().template data<char> ());
        std::stringstream inStream (receivedAnswer);
        {
          cereal::JSONInputArchive inputArchive (inStream);
          T result;
          inputArchive (result);
          this->setNextValue (result);
        }
        std::this_thread::yield ();
        std::this_thread::sleep_for (std::chrono::milliseconds (1));
      }
  }

  bool send ()
  {
    while (true)
      {
        try
          {
            outSocket.dial (uri, nng::flag::alloc);
            break;
          }
        catch (const std::exception &)
          {
            std::cout << "Connection to " << uri << " could not be established!\n";
          }
      }
    std::cout << "Connection to " << uri << " established\n";

    while (true)
      {
        tl::optional<T> dataOpt = this->dataProvider->getCurrentValue (this->uuid);
        if (dataOpt)
          {
            T data = dataOpt.value ();
            std::ostringstream stream;
            {
              cereal::JSONOutputArchive outputArchive (stream);
              outputArchive (data);
            }
            auto dataString = stream.str ();
            dataString = stream.str ();
            outSocket.send (nng::buffer (nng_strdup (dataString.c_str ()), dataString.length () + 1), nng::flag::alloc);
          }
        else
          {
            std::this_thread::yield ();
            std::this_thread::sleep_for (std::chrono::milliseconds (50));
          }
      }
  }

};
