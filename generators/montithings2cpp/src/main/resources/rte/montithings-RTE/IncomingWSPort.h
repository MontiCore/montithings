// (c) https://github.com/MontiCore/monticore
#pragma once
#include "Port.h"
#include "nngpp/nngpp.h"
#include "nngpp/protocol/pull0.h"
#include "nngpp/protocol/sub0.h"
#include "nngpp/protocol/bus0.h"
#include <nng/nng.h>
#include <nng/protocol/pubsub0/pub.h>
#include <nng/protocol/pubsub0/sub.h>
#include <iostream>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"
#include "rigtorp/SPSCQueue.h"
#include <future>

using namespace std;

/*
 * The class IPCPort extends the standard MontiArc Port with ipc capabilities in order to request data from
 * other processes running on the same machine.
 */
template<class T>
class IncomingWSPort : public Port<T>
{

  public:
  IncomingWSPort (const char *uri) : Port<T> ()
  {
    this->uri = uri;
    //Open Socket in Request mode
    socket = nng::sub::open ();
    nng_setopt (socket.get (), NNG_OPT_SUB_SUBSCRIBE, "", 0);
    //Dial specifies, that it connects to an already established socket (the server)
    try
      {
        socket.listen (uri, nng::flag::alloc);
      }
    catch (const std::exception &e)
      {
        cout << "Could not create listener for: " << uri << " (" << e.what () << ")\n";
        return;
      }
    cout << "Created listener for: " << uri << "\n";
    fut = std::async (std::launch::async, &IncomingWSPort::listen, this);
  };

  explicit IncomingWSPort (T initialValue) : Port<T>::Port (initialValue)
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
        return tl::nullopt;
      }
  }

  private:
  nng::socket socket;
  const char *uri;
  std::future<bool> fut;
  rigtorp::SPSCQueue<T> queue;

  bool listen ()
  {
    //Receive Message and convert to target type T
    while (true)
      {
        auto msg = socket.recv_msg ();
        auto data = msg.body ().template data<char> ();
        std::string receivedAnswer (msg.body ().template data<char> ());
        std::stringstream inStream (receivedAnswer);
        {
          cereal::JSONInputArchive inputArchive (inStream);
          T result;
          inputArchive (result);
          //std::cout << "Raw Data: " << result << "\n";
          this->pushToAll (result);
        }
        std::this_thread::yield ();
        std::this_thread::sleep_for (std::chrono::milliseconds (1));
      }
    return true;
  }

};