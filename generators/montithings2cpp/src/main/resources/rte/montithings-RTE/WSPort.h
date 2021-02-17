/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include <nngpp/socket.h>
#include <nngpp/protocol/sub0.h>
#include <nngpp/protocol/pub0.h>
#include <future>
#include <iostream>
#include "Port.h"
#include "Utils.h"
#include "easyloggingpp/easylogging++.h"

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
  bool outIsListener; // determines whether out or in port is listener
  std::atomic<bool> killSwitch;

  public:
  explicit WSPort (Direction direction, std::string uri, bool outIsListener = true)
      : direction (direction), outIsListener (outIsListener)
  {
    char * cstr = new char [uri.length()+1];
    std::strcpy (cstr, uri.c_str());
    this->uri = cstr;

    killSwitch = false;
    if (direction == INCOMING)
      {
        inSocket = nng::sub::open ();
        nng_setopt (inSocket.get (), NNG_OPT_SUB_SUBSCRIBE, "", 0);
        if (!outIsListener)
          {
            try
              {
                inSocket.listen (this->uri, nng::flag::alloc);
              }
            catch (const std::exception &e)
              {
                LOG(ERROR) << "Could not create listener for URI \"" << uri
                          << "\". Exception: " << e.what ();
                return;
              }
            LOG(DEBUG) << "Created listener for URI " << uri;
          }
        futInSocket = std::async (std::launch::async, &WSPort::accept, this);
      }
    else
      {
        outSocket = nng::pub::open ();
        nng::set_opt_reconnect_time_max (outSocket, 2000);
        nng::set_opt_reconnect_time_min (outSocket, 100);
        futOutSocket = std::async (std::launch::async, &WSPort::send, this);
      }
  }

  void getExternalMessages () override
  {
    // Intentionally not implemented.
    // Functionality is provided by accept() to be non blocking.
  }

  void sendToExternal (tl::optional<T> nextVal) override
  {
    // Intentionally not implemented.
    // Functionality is provided by send() to be non-blocking and to
  }

  void killThread ()
  {
    // Do not directly kill the threads.
    // The killswitch ensures each thread is stopped in a safe state
    // (e.g. not while sending data)
    killSwitch = true;
  }

  bool accept ()
  {
    if (outIsListener)
      {
        while (true)
          {
            if (killSwitch)
              { return false; }
            try
              {
                inSocket.dial (uri, nng::flag::alloc);
                break;
              }
            catch (const std::exception &e)
              {
                LOG(DEBUG) << "Could not create listener for URI \"" << uri
                          << "\". Exception: " << e.what ();

                // Do not make this process eat up all resources in an endless loop
                std::this_thread::sleep_for (std::chrono::seconds (1));
              }
          }
        LOG(DEBUG) << "Created listener for: " << uri;
      }

    //Receive Message and convert to target type T
    while (true)
      {
        if (killSwitch)
          { return false; }
        auto msg = inSocket.recv_msg ();
        auto data = msg.body ().template data<char> ();
        T result = jsonToData<T> (data);
        this->setNextValue (result);

        std::this_thread::yield ();
        std::this_thread::sleep_for (std::chrono::milliseconds (1));
      }
  }

  bool send ()
  {
    while (true)
      {
        if (killSwitch)
          { return false; }
        try
          {
            if (outIsListener)
              {
                outSocket.listen (uri, nng::flag::alloc);
              }
            else
              {
                outSocket.dial (uri, nng::flag::alloc);
              }
            break;
          }
        catch (const std::exception &)
          {
            LOG(DEBUG) << "Connection to \"" << uri << "\" could not be established!";

            // Do not make this process eat up all resources in an endless loop
            std::this_thread::sleep_for (std::chrono::seconds (1));
          }
      }
    LOG(DEBUG) << "Connection to \"" << uri << "\" established";

    while (true)
      {
        if (killSwitch)
          { return false; }
        tl::optional<T> dataOpt;
        dataOpt = this->getCurrentValue (this->uuid);
        if (dataOpt)
          {
            auto dataString = dataToJson (dataOpt);
            outSocket.send (nng::buffer (nng_strdup (dataString.c_str ()), dataString.length () + 1), nng::flag::alloc);
          }
        std::this_thread::yield ();
        std::this_thread::sleep_for (std::chrono::milliseconds (1));
      }
  }
};
