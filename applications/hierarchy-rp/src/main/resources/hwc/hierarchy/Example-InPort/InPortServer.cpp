#include "InPortServer.h"

namespace montithings {
namespace hierarchy {

void InPortServer::setup(){
 //ToDo: Fill Me if needed
}

int InPortServer::getData(){
  static int i = 0;
  return i++;
}
} // namespace hierarchy
} // namespace montithings

int
main(int argc, char **argv) try {
    auto server = montithings::hierarchy::InPortServer("ipc://source.ipc");
    server.setup();
    server.run();
    return 1;
} catch (const nng::exception &e) {
    fprintf(stderr, "%s: %s\n", e.who(), e.what());
    return 1;
}
