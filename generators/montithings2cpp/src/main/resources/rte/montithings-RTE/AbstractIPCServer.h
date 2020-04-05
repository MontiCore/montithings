// (c) https://github.com/MontiCore/monticore

#ifndef REQREP_ABSTRACTIPCSERVER_H
#define REQREP_ABSTRACTIPCSERVER_H

#include <string>
#include <nngpp/nngpp.h>
#include <nngpp/protocol/rep0.h>
#include <nngpp/protocol/req0.h>
#include <nngpp/platform/platform.h>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"
#include <nng/nng.h>

/**
 * External programs providing data extend this class and override the getData() method of
 * this class to provide data to the architecture
 */
template<typename T>
class AbstractIPCServer
{
  private:
  virtual T getData () = 0;
  std::string dataString = "";
  nng::socket sock;

  public:
  explicit AbstractIPCServer (const char *uri)
  {
    sock = nng::rep::open ();
    sock.listen (uri);
  }

  void run ()
  {
    while (true)
      {
        std::ostringstream stream;
        sock.recv ();
        T data = getData ();

        {
          cereal::JSONOutputArchive outputArchive (stream);
          outputArchive (data);
        }

        dataString = stream.str ();
        sock.send (nng::buffer (nng_strdup (dataString.c_str ()), dataString.length () + 1), nng::flag::alloc);

        std::cout << dataString << "\n";

        //sock.send(nng::view(dataString.c_str(), strlen(dataString.c_str()) + 1));
      }
  }
};

#endif //REQREP_ABSTRACTIPCSERVER_H
