// (c) https://github.com/MontiCore/monticore

#ifndef OUTPORTSERVER_ABSTRACTIPCCLIENT_H
#define OUTPORTSERVER_ABSTRACTIPCCLIENT_H

#include <string>
#include <nngpp/nngpp.h>
#include <nngpp/protocol/pull0.h>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"

template<typename T>
class AbstractIPCClient
{
  private:
  virtual void processData (T data) = 0;
  std::string dataString = "";
  nng::socket sock;
  public:
  explicit AbstractIPCClient (const char *uri)
  {
    sock = nng::pull::open ();
    sock.listen (uri);
  }

  void run ()
  {
    while (true)
      {
        std::ostringstream stream;
        auto msg = sock.recv_msg ();
        auto data = msg.body ().template data<char> ();

        std::string receivedAnswer (msg.body ().template data<char> ());
        std::stringstream inStream (receivedAnswer);
        cereal::JSONInputArchive inputArchive (inStream);
        T result;
        inputArchive (result);

        processData (result);
      }
  }

};

#endif //OUTPORTSERVER_ABSTRACTIPCCLIENT_H
