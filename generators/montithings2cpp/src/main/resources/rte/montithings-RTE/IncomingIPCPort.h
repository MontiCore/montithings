// (c) https://github.com/MontiCore/monticore
#pragma once
#include "Port.h"
#include "nngpp/nngpp.h"
#include "nngpp/protocol/req0.h"
#include <iostream>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"

using namespace std;

/*
 * The class IPCPort extends the standard MontiArc Port with ipc capabilities in order to request data from
 * other processes running on the same machine.
 */
template<class T>
class IncomingIPCPort : public Port<T>
{

  public:
  IncomingIPCPort (const char *uri) : Port<T> ()
  {
    this->uri = uri;
    //Open Socket in Request mode
    socket = nng::req::open ();
    //Dial specifies, that it connects to an already established socket (the server)
    try
      {
        socket.dial (uri, nng::flag::alloc);
      }
    catch (const std::exception &e)
      {
        cout << "Connection to " << uri << " could not be established! (" << e.what () << ")\n";
        return;
      }
    cout << "Connection to " << uri << " established\n";
  };

  explicit IncomingIPCPort (T initialValue) : Port<T>::Port (initialValue)
  {
  }

  tl::optional<T> getCurrentValue (sole::uuid uuid)
  {

    T queueElement;
    if (this->queueMap[uuid].front ())
      {
        queueElement = *(this->queueMap[uuid].front ());
        this->queueMap[uuid].pop ();
        tl::optional<T> currentValue = queueElement;
        return currentValue;
      }
    else
      {
        ipcUpdate ();
        if (this->queueMap[uuid].front ())
          {
            queueElement = *(this->queueMap[uuid].front ());
            this->queueMap[uuid].pop ();
            tl::optional<T> currentValue = queueElement;
            return currentValue;
          }
        else
          {
            return tl::nullopt;
          }
      }
  }

  private:
  nng::socket socket;
  const char *uri;
  bool isConnected = false;

  /**
   * Initialize the IPC Port
   */
  void initIPC ()
  {

  }

  void ipcUpdate ()
  {
    if (!isConnected)
      {

        try
          {
            socket.dial (uri, nng::flag::alloc);

          }
        catch (const std::exception &e)
          {
            cout << "Connection to " << uri << " could not be established! (" << e.what () << ")\n";
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

    this->pushToAll (result);
  }

  virtual bool hasValue (sole::uuid uuid)
  {
    if (!this->queueMap[uuid].front ())
      {
        ipcUpdate ();
      }
    return (this->queueMap[uuid].front ());
  }

};