#ifndef MTINTERFACE_TESTINTERFACE_H
#define MTINTERFACE_TESTINTERFACE_H
#include "MTInterface.h"

void setup(){
    montiInterface::initSerial(115200);
    montiInterface::initInterface();
    montiInterface::announce("/portsInject/julius/connect");
    montiInterface::sendCharArr("hello");
}

void loop(){
}

#endif //MTINTERFACE_TESTINTERFACE_H
