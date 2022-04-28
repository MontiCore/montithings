#include <iostream>
#include "MontiThingsConnector.h"

INITIALIZE_EASYLOGGINGPP

void onReceive(int value) {
  LOG (INFO) << "Received: " << value;
}

int main(int argc, char* argv[])
{
  MontiThingsConnector<int> mtc ("sink", onReceive);
  mtc.connectToBroker();
  mtc.wait();
}
