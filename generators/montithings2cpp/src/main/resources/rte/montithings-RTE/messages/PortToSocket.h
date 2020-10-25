/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include "Message.h"
#include <iostream>
#include <utility>

class PortToSocket : public Message
{
  protected:
  std::string localPort = "";
  std::string remotePort = "";
  std::string ipAndPort = "";

  public:
  PortToSocket (std::string message)
  {
    // parse message of format: 'local_port=X,ip=localhost:8080,remote_port=/a/b/c'
    if (message.length () > 0)
      {
        localPort = message.substr (11, message.find (",ip=") - 11);
        ipAndPort = message.substr (message.find (",ip=") + 4,
                                    message.find (",remote_port=") - message.find (",ip=") - 4);
        remotePort = message.substr (message.find (",remote_port=") + 13);
      }
  }

  PortToSocket (std::string localPort, std::string ipAndPort, std::string remotePort)
      : localPort (std::move (localPort)),
        ipAndPort (std::move (ipAndPort)),
        remotePort (std::move (remotePort))
  {
  }

  const std::string getLocalPort () const
  {
    return localPort;
  }

  const std::string getRemotePort () const
  {
    return remotePort;
  }

  const std::string getIpAndPort () const
  {
    return ipAndPort;
  }

  std::string toString () override
  {
    return "local_port=" + localPort +
           ",ip=" + ipAndPort +
           ",remote_port=" + remotePort;
  }

};
