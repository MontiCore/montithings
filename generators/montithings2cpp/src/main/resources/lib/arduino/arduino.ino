// (c) https://github.com/MontiCore/monticore

#ifndef MTINTERFACE_TESTINTERFACE_H
#define MTINTERFACE_TESTINTERFACE_H
#include "MTInterface.h"

void setup(){
    montithings::initSerial(115200);
    montithings::initInterface();

    //name of the sink (name given to the sink-instance on startup in the command line)
    String receiverName = "example";
    montithings::announce(receiverName, "counting");
    montithings::announce(receiverName, "alternating");
}

int count = 0;
int bit = 0;

void loop(){
    montithings::send(count, "counting");
    montithings::send(bit, "alternating");
    count++;
    bit = (bit == 0 ? 1 : 0);
    delay(1000);
}

#endif //MTINTERFACE_TESTINTERFACE_H
