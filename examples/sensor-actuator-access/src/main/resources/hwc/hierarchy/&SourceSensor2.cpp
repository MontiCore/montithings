// (c) https://github.com/MontiCore/monticore
#include <iostream>
#include "MontiThingsConnector.h"

INITIALIZE_EASYLOGGINGPP

int main(int argc, char* argv[]) {
  MontiThingsConnector<int> mtc ("source", nullptr);
  mtc.connectToBroker();
  int i = 0;
  while (1)
    {
      LOG (DEBUG) << "Sending '" << i << "'";
      mtc.send (i++);
      std::this_thread::sleep_for (std::chrono::seconds(2));
    }
}
